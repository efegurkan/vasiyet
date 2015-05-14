package model

import com.mysql.jdbc.exceptions.jdbc4
import datalayer.ContactDBHelper
import play.api.Logger
import play.api.libs.json._

import scala.util.Try

case class EditContactForm(id: Long, name: String, surname: String, email: String)

case class Contact(id: Option[Long],
                   name: String,
                   surname: String,
                   email: String){
  def gravatarHash:String ={
    MD5.hash(email.trim.toLowerCase())
  }
}

object Contact extends JSONConvertable[Contact] {
  override def toJSON(contact: Contact): JsValue = {
    val ret = Json.obj(
      "id" -> contact.id.get,
      "name" -> contact.name,
      "surname" -> contact.surname,
      "email" -> contact.email
    )

    ret
  }

  //todo email regex
  override def fromJSON(json: JsValue): Contact = {
    // get data from json
    val id = Try((json \ "id").as[String].toLong.ensuring(i => i >= 0))
    val name = (json \ "name").asOpt[String]
    val surname = (json \ "surname").asOpt[String]
    val email = (json \ "email").asOpt[String]
    //validate fields
    val idValid: Boolean = id.isSuccess
    val nameValid: Boolean = name.exists(n => n.size >= 1)
    val surnameValid: Boolean = surname.exists(n => n.size >= 1)
    val emailValid: Boolean = email.exists(e => e.size >= 1)

    if (idValid && nameValid && surnameValid && emailValid) {
      new Contact(id.toOption, name.get, surname.get, email.get)
    }
    else {
      throw new Exception("Contact Json is not valid")
    }
  }

  def editContact(form: Contact, loggedUserId: Long): Boolean = {
    try {
      val id = form.id.getOrElse(throw new Exception("Incoming data is corrupted"))

      //This is an add request
      if (id == 0) {
        println(loggedUserId)
        ContactDBHelper.addNewContact(loggedUserId, form.name, form.surname, form.email)
      }
      else {
        //This is an edit request
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

  def deleteContact(id: Long,sessionid: Long): Boolean = {
    try {
      ContactDBHelper.deleteContact(id,sessionid)

    } catch {
      case ex: Exception =>
        Logger.error("Contact deleteContact")
        Logger.error(ex.getMessage)
        Logger.error(ex.getCause.toString)
        false
    }
  }

  def getContactByUserId(contactid:Long,userid:Long):Contact = {
    try{
      ContactDBHelper.getContactByUserId(contactid,userid)
    }
    catch {
      case ex: Exception => Logger.error("Contact.getContact exception")
        ex.printStackTrace()
        throw new Exception("Something went wrong! We couldn't get the contact information.")
    }
  }


}

object MD5 {
  def hash(s: String) = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }
}