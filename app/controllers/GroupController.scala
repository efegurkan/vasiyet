package controllers

import datalayer.{ContactDBHelper, GroupDBHelper}
import model.{EditGroupForm, Group}
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

  /*implicit val addMemberData: Reads[AddMemberData] = (
    (JsPath \ "contactId").read[Option[Long]] and
      (JsPath \ "groupId").read[Option[Long]]
    )(AddMemberData.apply _)
*/
  def showAddGroup() = AuthAction { request =>
    val empty = new Group(new Some[Long](0), "");
    val id =request.session.get("LoggedUser").get.toLong
    Ok(views.html.logged.editgroup(empty, "Add",ContactDBHelper.getContactsByUserId(id)))
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
      Ok(views.html.logged.editgroup(group.get, "Edit",ContactDBHelper.getContactsByUserId(id)))
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

  def extractMemberAdditionData(json: JsValue): (Long, Long) = {
    val grIdValid: Boolean = Try((json \ "groupId").as[String].toLong.ensuring(i => i > 0)).isSuccess
    val contactIdValid: Boolean = Try((json \ "contactId").as[String].toLong.ensuring(i => i > 0)).isSuccess
    if (grIdValid && contactIdValid) {
      val groupId = Try((json \ "groupId").as[String].toLong).get
      val contactId = Try((json \ "contactId").as[String].toLong).get
      (groupId, contactId)
    }
    else
      throw new Exception("Incoming data is corrupted")
  }

  def addMember() = AuthAction(BodyParsers.parse.json) { request =>
    try {
      val memberData = extractMemberAdditionData(request.body)
      val isItSaved = Group.addMember(memberData)
      if (isItSaved)
        Ok(Json.obj("Status" -> "OK", "message" -> "Contact added to group."))
      else throw new Exception("Contact save to Group failed.")
    } catch {
      case ex: Exception => {
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }
  }

  //todo merge extractid methods
  def extractMemberId(json: JsValue): Long = {
    val idValid: Boolean = Try((json \ "id").as[String].toLong.ensuring(i => i > 0)).isSuccess
    if (idValid) {
      val id = Try((json \ "id").as[String].toLong).get
      id
    }
    else
      throw new Exception("Incoming data is corrupted")
  }

  def deleteMember() = AuthAction(BodyParsers.parse.json) { request =>
    try {
      val data = extractMemberAdditionData(request.body)
      val isItDeleted = Group.deleteMember(data)
      if (isItDeleted)
      //todo Inform User
        Ok(Json.obj("Status" -> "OK", "message" -> "Member deleted from group successfully"))
      else
        throw new Exception("Member deletion from group failed")
    } catch {
      case ex: Exception => {
        Logger.warn("Group member deletion exception")
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }
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
  def deleteGroup() = AuthAction(parse.json) {
    request =>
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
  }
}
