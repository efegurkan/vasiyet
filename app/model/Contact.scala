package model


case class Contact ( pId : Option[Long], 
				pName : String, 
				pSurname : String,
				pEmail : String){

  private val _id: Option[Long] = pId
  private val _name = pName
  private val _surname = pSurname
  private val _email = pEmail
  
  //getters
  def id = _id
  def name = _name
  def surname = _surname
  def email = _email
  

   
}