package controllers

import datalayer.{ContactDBHelper, GroupDBHelper}
import model.Contact
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import utility.AuthAction

import scala.util.Try

object ContactController extends Controller {

  def showPage = AuthAction { request =>
    val id = request.session.get("LoggedUser")
    //TODO exception cases
    val contacts = ContactDBHelper.getContactsByUserId(id.get.toLong)
    val groups = GroupDBHelper.getGroupsOfUser(Some(id.get.toLong))
    Ok(views.html.logged.contacts(contacts, groups))

  }

  def showAddContact() = AuthAction { request =>
    val empty = new model.Contact(new Some[Long](0), "", "", "")
    Ok(views.html.logged.editcontact(empty, "Add"))
  }

  def showEditContact(id: Long) = AuthAction { request =>
    //TODO inform user about Redirect
    Logger.warn(id.toString)
    val contact = ContactDBHelper.getContactById(id)
    Logger.warn(contact.toString)
    if (!contact.isDefined) {
      Redirect("/")
    }
    else {
      Ok(views.html.logged.editcontact(contact.get, "Edit"))
    }
  }

  //Handle edit contact request as JSON
  def editContactJson() = AuthAction(BodyParsers.parse.json) { implicit request =>
    try {
      //get contact from json
      val contact = Contact.fromJSON(request.body)
      //save contact and observe result.
      val isItSaved = Contact.editContact(contact, request.session.get("LoggedUser").get.toLong)
      if (isItSaved)
        Ok(Json.obj("Status"->"OK","message"->"Contact saved successfully."))
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
  def getDeleteJsonData(json: JsValue): Option[Long] = {
    val idValid: Boolean = Try((json \ "id").as[String].toLong.ensuring(i=> i>= 0)).isSuccess
    if (idValid) {
      val id = Try((json \ "id").as[String].toLong).toOption
      id
    }
    else
      throw new Exception("Incoming data is corrupted")
  }

  //Handle delete request as JSON
  def deleteContact() = AuthAction(BodyParsers.parse.json) { implicit request =>
    try {
      val contactIdJson = getDeleteJsonData(request.body)
      if (Contact.deleteContact(contactIdJson.get))
        Ok(Json.obj("Status"-> "OK","message"->"Contact deleted successfully"))
      else
        throw new Exception("Contact deletion failed")
    } catch {
      case ex: Exception =>
        val message = ex.getMessage
        Logger.error(message)
        BadRequest(Json.obj("Status" -> "KO", "message" -> message))
    }
  }
}
