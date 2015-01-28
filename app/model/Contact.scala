package model

import datalayer.ContactDBHelper
import play.api.Logger

case class EditContactForm(id: Long, name: String, surname: String, email: String)

case class Contact(id: Option[Long],
                   name: String,
                   surname: String,
                   email: String)

object Contact {

  def editContact(form: Contact): Boolean = {
    try {
      val id = form.id.get

      if (id == 0) // add
        ContactDBHelper.addNewContact(form.name, form.surname, form.email)
      else {
        //todo cleanup
        println("inside else")
        ContactDBHelper.updateContact(id, form.name, form.surname, form.email)
      }
    }catch {
      case ex :Exception =>
        Logger.error("Contact editContact")
        Logger.error(ex.getMessage)
        false
    }
  }

  def deleteContact(form: Long): Boolean = {
    //todo not implemented
    false
  }
}