package model

case class EditContactForm ( id : Option[Long], name : String, surname : String, email : String)

case class Contact ( id : Option[Long],
	              			name : String,
	  	            		surname : String,
	              			email : String)



object Contact {

	def editContact( form: EditContactForm) :  Boolean = {
		//todo not implemented
		//todo it can be add or edit according to id field.
		false
	}

	def deleteContact( form: EditContactForm) : Boolean ={
		//todo not implemented
		false
	}
}