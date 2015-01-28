package datalayer

import com.mysql.jdbc.exceptions.jdbc4
import model.Contact
import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._


object ContactDBHelper extends DBHelper[Contact] {

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
          INSERT INTO vasiyet.Contact
          VALUES (NULL,{name},{surname},{email})
          """).on("name"->pName, "surname"->pSurname, "email"->pEmail)
      
     val result : Option[Long] = query.executeInsert()

      result.isDefined
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
          UPDATE vasiyet.Contact
          SET name = {name}, surname = {surname}, email = {email}
          WHERE id = {id}
          """).on("id"-> pId,"name"->pName, "surname"->pSurname, "email"->pEmail)
      //todo cleanup
      val poncik = query.execute()
      println(pId, pName, pSurname, pEmail)
      println("query")
      println(query)
      println(poncik)
      poncik
    }
  }

  def getGroupContacts(pGroupId: Option[Long]): Option[List[Contact]] = {
    DB.withConnection{implicit c=>
      val query = SQL(
        """
          |SELECT * FROM vasiyet.Contact
          |WHERE id
          |IN( SELECT contactId from vasiyet.GroupContactLookup where groupId = {groupid})
        """.stripMargin).on("groupid"-> pGroupId)

      try{
        val result = query.executeQuery()
        val contactList: List[Contact]= result.as(parser *).toList
        Some(contactList)
      }catch {
        case ex : jdbc4.MySQLIntegrityConstraintViolationException=> {Logger.error(ex.getErrorCode.toString)
          throw new Exception("{'error':'Please contact us with this error code:'"+ex.getErrorCode + "}")
        }
        case e : Throwable =>{
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          e.printStackTrace()
          None
        }
      }
    }
  }

  def getContactById(pContactId: Long): Option[Contact] = {
    DB.withConnection{implicit c=>
      val query = SQL(
        """
          |Select * From vasiyet.Contact
          |Where {id} = id
        """.stripMargin).on("id"-> pContactId)

      val result = query.executeQuery()
      val ret = result.as(parser *).headOption
      ret

    }
  }
}