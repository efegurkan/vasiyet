package model

import com.mysql.jdbc.JDBC4MySQLConnection
import com.mysql.jdbc.exceptions.{jdbc4, MySQLStatementCancelledException}
import datalayer.ContactDBHelper
import play.api.Logger

case class EditContactForm(id: Long, name: String, surname: String, email: String)

case class Contact(id: Option[Long],
                   name: String,
                   surname: String,
                   email: String)

object Contact {

  def editContact(form: Contact, loggedUserId: Long): Boolean = {
    try {
      val id = form.id.get

      if (id == 0) {
        // add todo add contact relation also
        println(loggedUserId)
        ContactDBHelper.addNewContact(loggedUserId, form.name, form.surname, form.email)
      }
      else {
        //todo cleanup
        println("inside else")
        ContactDBHelper.updateContact(id, form.name, form.surname, form.email)
      }
    } catch {
      case mysqlException: jdbc4.MySQLIntegrityConstraintViolationException => {
        Logger.error(mysqlException.getErrorCode.toString)
        false
      }

      case ex: Exception =>
        Logger.error("Contact editContact")
        Logger.error(ex.getMessage)
        Logger.error(ex.getCause.toString)
        false
    }
  }

  def deleteContact(id: Long): Boolean = {
    try{
       ContactDBHelper.deleteContact(id)

    }catch {
      case ex: Exception =>
        Logger.error("Contact deleteContact")
        Logger.error(ex.getMessage)
        Logger.error(ex.getCause.toString)
        false
    }
    }


}