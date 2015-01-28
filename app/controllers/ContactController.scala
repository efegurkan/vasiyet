package controllers

import datalayer.{ContactDBHelper, GroupDBHelper}
import model.Contact
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import utility.AuthAction

import scala.util.Try

object ContactController extends Controller {

//  implicit val editContactForm: Reads[EditContactForm] = (
//    (JsPath \ "id").read[Long] and
//      (JsPath \ "name").read[String](Reads.minLength[String](1)) and
//      (JsPath \ "surname").read[String](Reads.minLength[String](1)) and
//      (JsPath \ "email").read[String](Reads.email)
//    )(EditContactForm.apply _)

  implicit val deleteContactData: Reads[Long] =
    (JsPath \ "id").read[Long]

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
      //    val nonEmpty = new model.Contact(new Some[Long](0),"This","is a","test")
      Ok(views.html.logged.editcontact(contact.get, "Edit"))
    }
  }

  def isContactValid(json: JsValue): Boolean = {
    val idValid: Boolean = Try((json \ "id").as[String].toLong).isSuccess
    val nameValid: Boolean = (json \ "name").asOpt[String].exists(n => n.size >= 1)
    val surnameValid: Boolean = (json \ "surname").asOpt[String].exists(n => n.size >= 1)
    val emailValid: Boolean = (json \ "email").asOpt[String].exists(e => e.size >= 1)
    //todo cleanup
    println(idValid, nameValid, surnameValid, emailValid)

    idValid && nameValid && surnameValid && emailValid

  }

  //todo cleanup
  def extractContactFromJson(json: JsValue): Contact = {
    println(json)
    val id = Try((json \ "id").as[String].toLong).toOption
    println(id)
    val name = (json \ "name").as[String]
    val surname = (json \ "surname").as[String]
    val email = (json \ "email").as[String]

    new Contact(id, name, surname, email)
  }


  //todo cleanup
  def editContactJson() = AuthAction(BodyParsers.parse.json) { implicit request =>
    try {
      //get json body
      println(request)
      println(request.body)
      val contactJson = request.body
      println(contactJson)

      println(isContactValid(contactJson))
      if (isContactValid(contactJson)) {
        val contact = extractContactFromJson(contactJson)
        println(contact)
        //save contact
        val isIt = Contact.editContact(contact)
        println(isIt)
        if (isIt)
          Ok("Contact saved successfully.")
        else
          throw new Exception("EditContact failed")

      }
      else throw new Exception

    } catch {
      case e: Exception => {

        e.printStackTrace()
        Logger.error(e.getMessage)
        //todo add error message and inform user
        BadRequest(Json.obj("Status" -> "KO", "message" -> "Contact save failed."))
      }
    }
  }

  //todo rework on this doesnt work as expected
  def deleteContact() = AuthAction(BodyParsers.parse.json) { implicit request =>
    val deleteData = request.body.validate[Long]

    deleteData.fold(
      errors => {
        BadRequest(Json.obj("Staus" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      data => {
        try {
          val isItDeleted = Contact.deleteContact(data)
          if (isItDeleted)
          //todo inform user
            Ok("Contact deleted successfully")
          else
          //todo inform user
            throw new Exception
        } catch {
          case ex: Exception => {
            Logger.error(ex.getMessage)
            //todo inform user
            BadRequest(Json.obj("Status" -> "KO", "message" -> "Contact deletion failed"))
          }
        }
      }
    )
  }
}
