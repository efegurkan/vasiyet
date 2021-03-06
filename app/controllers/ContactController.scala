package controllers

import datalayer.{ContactDBHelper, GroupDBHelper}
import model.Contact
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import utility.AuthAction

import scala.util.Try

object ContactController extends Controller {

  /*Auth check, only see your own contacts*/
  def showPage = AuthAction { request =>
    try {
      val id = request.session.get("userid")
      //TODO exception cases
      val contacts = ContactDBHelper.getContactsByUserId(id.get.toLong)
      val groups = GroupDBHelper.getGroupsOfUser(id.get.toLong)
      Ok(views.html.logged.contacts(contacts, groups))
    }
    catch {
      case ex: Exception => {
        Logger.error("ContactController.showPage exception")
        ex.printStackTrace()
        BadRequest("Something went wrong during the execution of the page. Please contact us about this problem.")
      }
    }
  }

  /*Auth check, empty contact*/
  def showAddContact() = AuthAction { request =>
    try {
      val empty = new model.Contact(new Some[Long](0), "", "", "")
      Ok(views.html.logged.editcontact(empty, "Add"))
    }
    catch {
      case ex: Exception => {
        Logger.error("ContactController.showAddContact exception")
        ex.printStackTrace()
        BadRequest("Something went wrong during the execution of the page. Please contact us about this problem.")
      }
    }
  }

  /*Auth check, only edit own contacts*/
  def showEditContact(id: Long) = AuthAction { request =>
    try {
      val sessionid = request.session.get("userid").get.toLong
      val contact = Contact.getContactByUserId(id,sessionid)
      Ok(views.html.logged.editcontact(contact, "Edit"))

    } catch {
      case ex: Exception =>
        Logger.error("ContactController.showEditContact exception")
        Redirect("/contacts")
    }
  }

  /*Auth check, edit request only your own contacts*/
  //Handle edit contact request as JSON
  def editContactJson() = AuthAction(BodyParsers.parse.json) { implicit request =>
    try {
      //get contact from json
      val contact = Contact.fromJSON(request.body)
      //save contact and observe result.
      val isItSaved = Contact.editContact(contact, request.session.get("userid").get.toLong)
      if (isItSaved)
        Ok(Json.obj("Status" -> "OK", "message" -> "Contact saved successfully."))
      else
        throw new Exception("EditContact failed")
    } catch {
      case e: Exception => {
        e.printStackTrace()
        Logger.error(e.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> "Contact save failed."))
      }
    }
  }

  //extract delete request and validate
  def extractDeleteJsonData(json: JsValue): Option[Long] = {
    val idValid: Boolean = Try((json \ "id").as[String].toLong.ensuring(i => i > 0)).isSuccess
    if (idValid) {
      val id = Try((json \ "id").as[String].toLong).toOption
      id
    }
    else
      throw new Exception("Incoming data is corrupted")
  }

  /*Auth check, only delete a contact from own contacts*/
  //Handle delete request as JSON
  def deleteContact() = AuthAction(BodyParsers.parse.json) { implicit request =>
    try {
      val sessionid = request.session.get("userid").get.toLong
      val contactId = extractDeleteJsonData(request.body)
      if (Contact.deleteContact(contactId.get,sessionid))
        Ok(Json.obj("Status" -> "OK", "message" -> "Contact deleted successfully"))
      else
        throw new Exception("Contact deletion failed")
    } catch {
      case ex: Exception =>
        val message = ex.getMessage
        Logger.error(message)
        BadRequest(Json.obj("Status" -> "KO", "message" -> message))
    }
  }
  /*Auth check, only get own contacts*/
  def getContactsAutoComplete() = AuthAction { implicit request =>
    val userid = request.session.get("userid")
    try {
      //    val useridValid: Boolean = Try((request.body \ "userid").as[String].toLong.ensuring(i => i > 0)).isSuccess
      //    if (useridValid) {
      //      val userid = (request.body \ "userid").as[String].toLong


      //      val ret = Group.getMembers(groupId)
      val contacts = ContactDBHelper.getContactsByUserId(userid.get.toLong)
      if (contacts.isEmpty) {
        val ret = Json.obj("Contact" -> "You don't have any contact")
        Ok(ret)
      } else {
        Json.arr()

        val list = contacts.map(f => Contact.toJSON(f))
        val ret = list.foldLeft(JsArray())((acc, x) => acc ++ Json.arr(x))
        Ok(ret)
      }


    }

    catch {
      case ex: Exception =>
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
    }

  }
}
