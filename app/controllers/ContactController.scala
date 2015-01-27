package controllers

import datalayer.{ContactDBHelper, GroupDBHelper}
import model.{Contact, EditContactForm}
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import utility.AuthAction

object ContactController extends Controller{

  implicit val editContactForm: Reads[EditContactForm] = (
    (JsPath \ "id").read[Option[Long]] and
     (JsPath \ "name").read[String](Reads.minLength[String](1)) and
     (JsPath \ "surname").read[String](Reads.minLength[String](1)) and
     (JsPath \ "email").read[String](Reads.email)
    )(EditContactForm.apply _)

  implicit val deleteContactData: Reads[Long] =
    (JsPath \ "id").read[Long]

  def showPage = AuthAction{request =>
    val id = request.session.get("LoggedUser")
    //TODO exception cases
    val contacts = ContactDBHelper.getContactsByUserId(id.get.toLong)
    val groups =GroupDBHelper.getGroupsOfUser(Some(id.get.toLong))
    Ok(views.html.logged.contacts( contacts , groups ) )

  }

  def showAddContact() = AuthAction{request =>
    val empty = new model.Contact(new Some[Long](0),"","","")
    Ok(views.html.logged.editcontact(empty,"Add"))
  }

  def showEditContact(id: Long) = AuthAction{request =>
    //TODO inform user about Redirect
    Logger.warn(id.toString)
    val contact = ContactDBHelper.getContactById(id)
    Logger.warn(contact.toString)
    if(!contact.isDefined){Redirect("/")}
    else{
    //    val nonEmpty = new model.Contact(new Some[Long](0),"This","is a","test")
      Ok(views.html.logged.editcontact(contact.get, "Edit"))}
    }

  def editContactJson() = AuthAction(BodyParsers.parse.json){ implicit request =>
   val contactFormData = request.body.validate[EditContactForm]

   contactFormData.fold(
     errors => {
       BadRequest(Json.obj("Staus" -> "KO", "message" -> JsError.toFlatJson(errors)))
     },
     data => {
       try{
         val isItSaved = Contact.editContact(data)
         //todo inform user about success.
         if(isItSaved)
         Ok("Contact saved successfully.")
         else
         //todo send error message
           throw new Exception(isItSaved.toString)
       }catch{
         case e: Exception => {
           Logger.error(e.getMessage)
           //todo add error message and inform user
           BadRequest(Json.obj("Status"-> "KO", "message"-> "Contact save failed."))
         }
       }
     }
   )

  }

  //todo rework on this doesnt work as expected
  def deleteContact() = AuthAction(BodyParsers.parse.json){implicit request =>
    val deleteData = request.body.validate[Long]

    deleteData.fold(
      errors=>{
        BadRequest(Json.obj("Staus" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      data=>{
        try{
          val isItDeleted = Contact.deleteContact(data)
          if (isItDeleted)
            //todo inform user
            Ok("Contact deleted successfully")
          else
            //todo inform user
            throw new Exception
        }catch {
          case ex: Exception => {
            Logger.error(ex.getMessage)
            //todo inform user
            BadRequest(Json.obj("Status"->"KO","message"->"Contact deletion failed"))
          }
        }
      }
    )
  }
}
