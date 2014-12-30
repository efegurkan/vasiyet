package model

import datalayer.UserDBHelper

case class LoginForm(email:String, password : String)
case class RegisterForm(email:String, password: String, name: String, surname: String)

case class User( pId : Option[Long],
				 pEmail : String,
				 pName : String,
				 pSurname : String) {
  
  private val _id = pId
  private val _email = pEmail
  private val _name = pName
  private val _surname = pSurname
  
  //Getters
  def id = _id
  def email = _email
  def name = _name
  def surname = _surname
  

}

object User {
  
    def login( pEmail: String, pPassword: String) : Option[User] = {
    UserDBHelper.loginUser(pEmail, pPassword)
    
    }

    def loginJson(form : LoginForm): Option[User] ={
      UserDBHelper.loginUser(form.email,form.password)
    }
    
    def register(pEmail:String, pPassword:String, pName:String, pSurname:String):Option[User]={
      UserDBHelper.createUser(pEmail, pPassword, pName, pSurname)
      login(pEmail,pPassword)

    }

    def registerJson(form:RegisterForm): Option[User] ={
      UserDBHelper.createUser(form.email,form.password,form.name,form.surname)
      login(form.email,form.password)
    }
}