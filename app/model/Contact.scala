package model

import com.mysql.jdbc.JDBC4MySQLConnection
import com.mysql.jdbc.exceptions.{jdbc4, MySQLStatementCancelledException}
import datalayer.ContactDBHelper
import play.api.Logger
import play.api.libs.json._

import scala.util.Try

case class EditContactForm(id: Long, name: String, surname: String, email: String)

case class Contact(id: Option[Long],
                   name: String,
                   surname: String,
                   email: String)

object Contact extends JSONConvertable[Contact] {
  //todo complete toJSON
  override def toJSON(contact: Contact): JsValue = ???

  //todo email regex
  override def fromJSON(json : JsValue): Contact =
  {
    // get data from json
    val id = Try((json \ "id").as[String].toLong.ensuring(i=>i>=0))
    val name = (json \ "name").asOpt[String]
    val surname = (json \ "surname").asOpt[String]
    val email = (json \ "email").asOpt[String]
    //validate fields
    val idValid: Boolean = id.isSuccess
    val nameValid: Boolean = name.exists(n => n.size >= 1)
    val surnameValid: Boolean = surname.exists(n => n.size >= 1)
    val emailValid: Boolean = email.exists(e => e.size >= 1)

    if(idValid && nameValid && surnameValid && emailValid)
    {
      new Contact(id.toOption,name.get,surname.get,email.get)
    }
    else
    {
      throw new Exception("Contact Json is not valid")
    }
  }
  def editContact(form: Contact, loggedUserId: Long): Boolean = {
    try {
      val id = form.id.get

      //This is an add request
      if (id == 0) {
        println(loggedUserId)
        ContactDBHelper.addNewContact(loggedUserId, form.name, form.surname, form.email)
      }
      else {//This is an edit request
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