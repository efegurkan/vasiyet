package controllers

import datalayer.GroupDBHelper
import model.{AddMemberData, EditGroupForm, Group}
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{BodyParsers, Controller}
import utility.AuthAction

import scala.util.Try

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
    try {
      val group = Group.fromJSON(request.body)
      val isItSaved = Group.editGroup(group, request.session.get("LoggedUser").get.toLong);

      if (isItSaved)
        Ok("Group save successful.")
      else
        throw new Exception("Group save failed")
    } catch {
      case ex: Exception =>
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
    }
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

  //extract group id from JSON for deletion
  def extractDeleteGroupData(json: JsValue): Long = {
    val idValid: Boolean = Try((json \ "id").as[String].toLong.ensuring(i => i > 0)).isSuccess
    if (idValid) {
      val id = Try((json \ "id").as[String].toLong).get
      id
    }
    else
      throw new Exception("Incoming data is corrupted")
  }

  //Handle group delete request as JSON
  def deleteGroup() = AuthAction(parse.json) { request =>
    try {
      val data = extractDeleteGroupData(request.body)
      val isItDeleted = Group.deleteGroup(data)
      if (isItDeleted)
        Ok(Json.obj("Status" -> "OK", "message" -> "Group deleted successfully"))
      else
        throw new Exception("Group deletion failed")
    } catch {
      case ex: Exception => {
        Logger.warn("Group deletion exception")
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }
    //      }
    //    )

  }
}
