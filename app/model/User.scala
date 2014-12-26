package model

import datalayer.UserDBHelper

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
    
    def register(pEmail:String, pPassword:String, pName:String, pSurname:String):Option[User]={
      UserDBHelper.createUser(pEmail, pPassword, pName, pSurname)
      login(pEmail,pPassword)

    }
}