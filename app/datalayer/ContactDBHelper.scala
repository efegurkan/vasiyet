package datalayer

import com.mysql.jdbc.exceptions.jdbc4
import model.Contact
import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._


object ContactDBHelper extends DBHelper[Contact] {

  def parser: RowParser[Contact] = {
    get[Option[Long]]("id") ~
      get[String]("name") ~
      get[String]("surname") ~
      get[String]("email") map {
      case id ~ name ~ surname ~ email => Contact(id, name, surname, email)
    }
  }

  def getContactsByUserId(pUserId: Long): List[Contact] = {
    DB.withConnection { implicit c =>
      val query = SQL( """
           SELECT * FROM vasiyet.Contact 
           WHERE id 
           IN (Select contactid from vasiyet.UserLookup where userid= {userid} )
                       """).on("userid" -> pUserId)
      val result = query.executeQuery()
      val contacts: List[Contact] = result.as(parser *).toList
      contacts
    }

  }

  def addNewContact(pUserId: Long, pName: String,
                    pSurname: String,
                    pEmail: String): Boolean = {

    try {
      DB.withTransaction { implicit c =>
        val insertedId : Option[Long] = SQL("INSERT INTO Contact VALUES (NULL,{name},{surname},{email})").
        on("name"->pName, "surname"->pSurname, "email"->pEmail).executeInsert()

        SQL( "INSERT INTO UserLookup  VALUES (NULL,{userId},{lastid})").on("userId"->pUserId,"lastid"->insertedId.get).executeInsert()

        true
      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Logger.error(ex.toString)
        println("db seysi")
        println(ex.getCause.getMessage)
        println(ex.getMessage)
        false
    }

  }

  def bindContactToUser(userId: Long, contactId: Long): Boolean = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |INSERT INTO vasiyet,UserLookup
          |VALUES (NULL, {userId}, {contactId})
        """.stripMargin).on("userId" -> userId, "contactId" -> contactId)

      val result: Option[Long] = query.executeInsert()

      result.isDefined
    }

    false
  }


  def deleteContact(pId: Long): Boolean = {
    println(pId)
    DB.withConnection { implicit c =>
      val query = SQL( """ DELETE FROM vasiyet.Contact WHERE id = {id} """.stripMargin).on("id" -> pId)

      //If no rows affected it is false
      query.executeUpdate() !=0
    }
  }

  def updateContact(pId: Long,
                    pName: String,
                    pSurname: String,
                    pEmail: String): Boolean = {
    DB.withConnection { implicit c =>
      val query = SQL( """
          UPDATE Contact
          SET name = {name},
          surname = {surname},
          email = {email}
          WHERE id = {id}
                       """.stripMargin).on("id" -> pId, "name" -> pName, "surname" -> pSurname, "email" -> pEmail)
      //If no rows affected it is false
      query.executeUpdate() != 0
    }
  }

  def getGroupContacts(pGroupId: Option[Long]): Option[List[Contact]] = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |SELECT * FROM vasiyet.Contact
          |WHERE id
          |IN( SELECT contactId from vasiyet.GroupContactLookup where groupId = {groupid})
        """.stripMargin).on("groupid" -> pGroupId)

      try {
        val result = query.executeQuery()
        val contactList: List[Contact] = result.as(parser *).toList
        Some(contactList)
      } catch {
        case ex: jdbc4.MySQLIntegrityConstraintViolationException => {
          Logger.error(ex.getErrorCode.toString)
          throw new Exception("{'error':'Please contact us with this error code:'" + ex.getErrorCode + "}")
        }
        case e: Throwable => {
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          e.printStackTrace()
          None
        }
      }
    }
  }

  def getContactById(pContactId: Long): Option[Contact] = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |Select * From vasiyet.Contact
          |Where {id} = id
        """.stripMargin).on("id" -> pContactId)

      val result = query.executeQuery()
      val ret = result.as(parser *).headOption
      ret

    }
  }
}