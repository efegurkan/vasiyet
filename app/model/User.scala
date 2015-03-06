package model

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException
import datalayer.UserDBHelper

case class LoginForm(email: String, password: String)

case class RegisterForm(email: String, password: String, name: String, surname: String)

case class User(id: Long,
                email: String,
                name: String,
                surname: String)

object User {

  //    def login( pEmail: String, pPassword: String) : Option[User] = {
  //    UserDBHelper.loginUser(pEmail, pPassword)
  //
  //    }

  def loginJson(form: LoginForm): Option[User] = {
    try {
      UserDBHelper.loginUser(form.email, form.password)
    }
    catch {
      //TODO make exceptions logical on here
      case e: Exception => throw new Exception(e.getMessage)
    }
  }

  //    def register(pEmail:String, pPassword:String, pName:String, pSurname:String):Option[User]={
  //      UserDBHelper.createUser(pEmail, pPassword, pName, pSurname)
  //      login(pEmail,pPassword)
  //
  //    }

  def registerJson(form: RegisterForm): Option[User] = {
    try {
      UserDBHelper.createUser(form.email, form.password, form.name, form.surname)
      val loginForm = new LoginForm(form.email, form.password)
      loginJson(loginForm)
    }
    catch {
      case e: Exception => throw new Exception(e.getMessage)
    }
  }
}