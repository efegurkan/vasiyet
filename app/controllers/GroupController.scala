package controllers

import datalayer.GroupDBHelper
import model.{AddMemberData, EditGroupForm, Group}
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsPath, Json, Reads}
import play.api.mvc.{BodyParsers, Controller}
import utility.AuthAction

object GroupController extends Controller {
  implicit val editGroupForm: Reads[EditGroupForm] = (
    (JsPath \ "id").read[Option[Long]] and
      (JsPath \ "name").read[String](Reads.minLength[String](1))
    )(EditGroupForm.apply _)

  implicit val addMemberData: Reads[AddMemberData] = (
    (JsPath \ "contactId").read[Option[Long]] and
      (JsPath \ "groupId").read[Option[Long]]
    )(AddMemberData.apply _)

  def showAddGroup() = AuthAction { request =>
    val empty = new Group(new Some[Long](0), "");
    Ok(views.html.logged.editgroup(empty, "Add"))
  }

  def showEditGroup(id: Long) = AuthAction { request =>
    //TODO inform user about Redirect
    Logger.warn(id.toString)
    val group = GroupDBHelper.getGroupById(Option(id))
    if (!group.isDefined) {
      Redirect("/")
    }
    else
    //    val nonEmpty = new Group(new Some[Long](0),"")
      Ok(views.html.logged.editgroup(group.get, "Edit"))
  }

  def editGroup() = AuthAction(BodyParsers.parse.json) { implicit request =>
    val formData = request.body.validate[EditGroupForm]

    formData.fold(
      errors => {
        BadRequest(Json.obj("Status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      data => {
        try {
          val isItSaved = Group.editGroup(data);

          if (isItSaved)
          //todo inform user about success
            Ok("Group save successful.")
          else
            throw new Exception()
        } catch {
          case ex: Exception => {
            BadRequest("Group save failed")
          }
        }
      }
    )
  }

  def addMember() = AuthAction(BodyParsers.parse.json) { request =>
    val jsonData = request.body.validate[AddMemberData]

    jsonData.fold(
      errors => {
        BadRequest(Json.obj("Status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      data => {
        try {
          val isItSaved = Group.addMember(data)
          if (isItSaved)
          //todo inform user
            Ok("Contact added to group.")
          else throw new Exception
        } catch {
          case ex: Exception => {
            Logger.error(ex.getMessage)
            BadRequest("Contact save to Group failed.")
          }
        }
      }
    )
  }

  def deleteMember() = AuthAction(BodyParsers.parse.json) { request =>
    val jsonData = request.body.validate[AddMemberData]

    jsonData.fold(
      errors => {
        BadRequest(Json.obj("Status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      data => {
        try {
          val isItDeleted = Group.deleteMember(data)
          if (isItDeleted)
          //todo Inform User
            Ok("Contact deleted from group")
          else
            throw new Exception
        } catch {
          case ex: Exception => {
            Logger.error(ex.getMessage)
            BadRequest("Contact deletion from Group failed.")
          }
        }
      }
    )
  }

  //todo rework on this
  def deleteGroup() = AuthAction(parse.json) { request =>
    val jsonData = request.body.validate[Long]
    jsonData.fold(
      errors => {
        BadRequest(Json.obj("Staus" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      data => {
        try {
          val isItDeleted = Group.deleteGroup(data)
          if (isItDeleted)
          //todo inform user
            Ok("Group deleted successfully")
          else
          //todo inform user
            throw new Exception
        } catch {
          case ex: Exception => {
            Logger.warn("Exception happened")
            Logger.error(ex.getMessage)
            //todo inform user
            BadRequest(Json.obj("Status" -> "KO", "message" -> "Group deletion failed"))
          }
        }
      }
    )

  }
}
