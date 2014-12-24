package datalayer

import model.Contact
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._


class ContactDBHelper extends DBHelper[Contact] {
  
  def parser : RowParser[Contact] = {
    get[Option[Long]]("id") ~ 
    get[String]("name") ~ 
    get[String]("surname") ~ 
    get [String]("email")map{
      case id ~ name ~ surname ~ email => Contact(id,name,surname,email)
    }
  }
  
  def getContactsByUserId ( pUserId : Long) : List[Contact]={
    DB.withConnection{ implicit c =>
      val query = SQL("""
           SELECT * FROM vasiyet.Contact 
           WHERE id 
           IN (Select contactid from vasiyet.UserLookup where userid= {userid} )
           """).on("userid" -> pUserId)
       val result = query.executeQuery()
       val contacts: List[Contact] = result.as(parser *).toList
       contacts
     }
     
   }
  
  def addNewContact ( pName : String, 
		  			  pSurname : String,
		  			  pEmail : String ) : Boolean = {
    
    DB.withConnection{ implicit c =>
      val query = SQL("""
          INSERT INTO Contact
          VALUES ({name},{surname},{email})
          """).on("name"->pName, "surname"->pSurname, "email"->pEmail)
      
     query.execute
      
    }
    
  }
  
  
  def deleteContact (pId : Long) : Boolean = {
    DB.withConnection{ implicit c =>
      val query = SQL("""
          DELETE FROM Contact
          WHERE id = {id}
          """).on("id"->pId)
      
      query.execute()
    }
  }
  
  def updateContact( pId : Long,
		  			 pName : String,
		  			 pSurname : String,
		  			 pEmail : String) : Boolean = {
    DB.withConnection{ implicit c =>
      val query = SQL("""
          UPDATE Contact
          SET name = {name}, surname = {surname}, email = {email}
          WHERE id = {id}
          """).on("id"-> pId,"name"->pName, "surname"->pSurname, "email"->pEmail)
      
      query.execute()
    }
  }


}