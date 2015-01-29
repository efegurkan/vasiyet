package model

import com.mysql.jdbc.exceptions.jdbc4
import datalayer.GroupDBHelper
import play.api.Logger
import play.api.libs.json.JsValue

import scala.util.Try

case class EditGroupForm(id: Option[Long],
                         name: String
                          )

case class Group(id: Option[Long],
                 name: String,
                 members: List[Contact] = List.empty
                  )

case class AddMemberData(contactId: Option[Long], groupId: Option[Long])

object Group extends JSONConvertable[Group] {
  def editGroup(form: Group, loggedUserId: Long): Boolean = {
    val id = form.id.getOrElse(throw new Exception("Incoming data is corrupted"))
    try {
      if (id == 0) {
        //This is an add request
        GroupDBHelper.createGroup(form.name, loggedUserId)
      }
      else //This is an edit request
        GroupDBHelper.updateGroup(Some(id), form.name)
    }
    catch {
      case mysqlException: jdbc4.MySQLIntegrityConstraintViolationException => {
        Logger.error(mysqlException.getErrorCode.toString)
        false
      }
      case ex: Exception => {
        Logger.error("Group editGroup")
        Logger.error(ex.getMessage)
        Logger.error(ex.getCause.toString)
        false
      }
    }
  }

  def deleteGroup(groupId: Long): Boolean = {
    try {
      GroupDBHelper.deleteGroup(groupId)

    } catch {
      case ex: Exception =>
        Logger.error("Contact deleteContact")
        Logger.error(ex.getMessage)
        Logger.error(ex.getCause.toString)
        false
    }
  }

  def addMember(data: AddMemberData): Boolean = {
    //todo not implemented
    false
  }

  def deleteMember(data: AddMemberData): Boolean = {
    //todo not implemented
    false
  }

  override def toJSON(t: Group): JsValue = ???

  //todo get members?
  override def fromJSON(json: JsValue): Group = {

    // get data from json
    val id = Try((json \ "id").as[String].toLong.ensuring(i => i >= 0))
    val name = (json \ "name").asOpt[String]
    //validate fields
    val idValid: Boolean = id.isSuccess
    val nameValid: Boolean = name.exists(n => n.size >= 1)

    if (idValid && nameValid) {
      new Group(id.toOption, name.get)
    }
    else {
      throw new Exception("Group Json is not valid")
    }
  }
}
