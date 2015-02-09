package model

import com.mysql.jdbc.exceptions.jdbc4
import datalayer.{ContactDBHelper, GroupDBHelper}
import play.api.Logger
import play.api.libs.json.{JsArray, JsValue, Json}

import scala.util.Try

case class EditGroupForm(id: Option[Long],
                         name: String
                          )

case class Group(id: Option[Long],
                 name: String,
                 members: List[Contact] = List.empty
                  )

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
        Logger.error("delete Group")
        Logger.error(ex.getMessage)
        Logger.error(ex.getCause.toString)
        false
    }
  }

  def addMember(data: (Long, Long)): Boolean = {
    val groupid = data._1
    val contactid = data._2

    try {
      GroupDBHelper.addMember(groupid, contactid)
    }
    catch {
      //todo exception handling review
      case ex: Exception =>
        Logger.error("Group addMember")
        Logger.error(ex.getMessage)
        Logger.error(ex.getCause.toString)
        false
    }
  }

  def deleteMember(data: (Long, Long)): Boolean = {
    //todo not implemented
    val groupid = data._1
    val contactid = data._2

    try {
      GroupDBHelper.deleteMember(groupid, contactid)
    }
    catch {
      //todo exception handling review
      case ex: Exception =>
        Logger.error("Group deleteMember")
        Logger.error(ex.getMessage)
        Logger.error(ex.getCause.toString)
    }
    false
  }

  def getMembersAsJson(data: Long): JsArray = {
    try {
      val members = ContactDBHelper.getGroupContacts(Some(data))
      if (members.isDefined) {
        val list = members.get
        val lst = list.map(f => Contact.toJSON(f))
        val ret = lst.foldLeft(JsArray())((acc, x) => acc ++ Json.arr(x))
        ret
      }
      else
        throw new Exception

    }
    catch {
      case ex: Exception =>
        Logger.error("Group getMembers")
        Logger.error(ex.getMessage)
        ex.printStackTrace()
        throw new Exception("Couldn't load member data")
    }
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
