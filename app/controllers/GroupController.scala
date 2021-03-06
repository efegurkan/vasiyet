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

  /*Auth check*/
  def showAddGroup() = AuthAction { request =>
    val empty = new Group(new Some[Long](0), "")
    val id = request.session.get("userid").get.toLong
    Ok(views.html.logged.editgroup(empty, "Add", ContactDBHelper.getContactsByUserId(id)))
  }

  /*Auth check, only show own groups*/
  def showEditGroup(id: Long) = AuthAction { request =>
    //TODO inform user about Redirect

    val sessionid = request.session.get("userid").get.toLong
    val group = GroupDBHelper.getGroupByIdWithUser(id,sessionid)
    if (!group.isDefined) {
      Redirect("/")
    }
    else
      Ok(views.html.logged.editgroup(group.get, "Edit", ContactDBHelper.getContactsByUserId(id)))
  }

  def editGroup() = AuthAction(BodyParsers.parse.json) { implicit request =>
    try {
      val group = Group.fromJSON(request.body)
      val isItSaved = Group.editGroup(group, request.session.get("userid").get.toLong)

      if (isItSaved._1)
        Ok(Json.obj("Status" -> "OK", "message" -> "Group saved successfully", "groupId" -> isItSaved._2))
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

  /*Auth check*/
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

  /*Auth check, only delete own members from own groups*/
  def deleteMember() = AuthAction(BodyParsers.parse.json) { request =>
    try {
      val sessionid = request.session.get("userid").get.toLong
      val data = extractMemberAdditionData(request.body)
      val isItDeleted = Group.deleteMember(data,sessionid)
      if (isItDeleted)
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

  /*Auth check, only delete own Groups*/
  //Handle group delete request as JSON
  def deleteGroup() = AuthAction(BodyParsers.parse.json) {
    request =>
      try {
        val sessionid = request.session.get("userid").get.toLong
        val data = extractDeleteGroupData(request.body)
        val isItDeleted = Group.deleteGroup(data,sessionid)
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

  /*Auth check, only get own groups*/
  def getGroupsJson() = AuthAction { request =>
    try {
      val id = request.session.get("userid").get.toLong
      val raw = GroupDBHelper.getGroupsOfUser(id)
      val jsonlist = raw.map(g => Group.toJSON(g))
      println(jsonlist)
      val groups = Json.toJson(jsonlist)
      Ok(groups)
//      BadRequest(Json.obj("Status" -> "KO", "message" ->"dsadsa"))
    } catch {
      case ex: Exception => {
        Logger.warn("Group retrive exception")
        Logger.error(ex.getMessage)
        ex.printStackTrace
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }
  }

}
