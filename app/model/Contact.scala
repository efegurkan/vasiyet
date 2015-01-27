package model

case class EditContactForm ( id : Option[Long], name : String, surname : String, email : String)

case class Contact ( id : Option[Long],
	              			name : String,
	  	            		surname : String,
	              			email : String)



object Contact {

	def updateContact( form: EditContactForm) : Boolean ={
		//todo not implemented
		false
	}

	def addContact( form: EditContactForm) :  Boolean = {
		//todo not implemented
		false
	}
}