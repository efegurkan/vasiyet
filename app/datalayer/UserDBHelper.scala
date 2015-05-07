package datalayer

import com.mysql.jdbc.exceptions.{jdbc4, MySQLIntegrityConstraintViolationException}
import model.User
import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

object UserDBHelper extends DBHelper[User] {

  def parser: RowParser[User] = {
    get[Long]("id") ~
      get[String]("email") ~
      get[String]("name") ~
      get[String]("surname") ~
      get[Boolean]("isDead") map {
      case id ~ email ~ name ~ surname ~ isDead=> User(id, email, name, surname, isDead)
    }

  }

  def createUser(pEmail: String,
                 pPassword: String,
                 pName: String,
                 pSurname: String): Boolean = {

    DB.withConnection { implicit c =>
      val query = SQL( """
        INSERT INTO User
        VALUES( NULL, {email}, {password}, {name}, {surname}, '0' )
                       """).on("email" -> pEmail, "password" -> pPassword, "name" -> pName, "surname" -> pSurname)

      try {
        query.execute()
      }
      catch {
        case ex: jdbc4.MySQLIntegrityConstraintViolationException => {
          Logger.error(ex.getErrorCode.toString)
          if (ex.getErrorCode == 1062)
            throw new Exception("{'error':'This mail adress is already in use!'}")
          else
            throw new Exception("{'error':'Please contact us with this error code:'" + ex.getErrorCode + "}")
        }
        case e: Throwable => {
          e.printStackTrace()
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          false
        }
      }
    }
  }


  def deleteUser(pId: Long): Boolean = {
    DB.withConnection { implicit c =>
      val query = SQL( """
          DELETE FROM User
          WHERE {id} = id
                       """).on("id" -> pId)

      query.execute
    }
  }

  def updateUser(pId: Long,
                 pEmail: String,
                 pPassword: String,
                 pName: String,
                 pSurname: String,
                 pNewPassword: String): Boolean = {
    DB.withConnection { implicit c =>
      val query1 = SQL( """
          UPDATE User
          SET email={email}, password = {newpassword}, name = {name}, surname = {surname}
          WHERE id = {id} AND password = {password}
                        """).on("email" -> pEmail, "password" -> pPassword, "name" -> pName,
          "surname" -> pSurname, "newpassword" -> pNewPassword)

      val query2 = SQL( """
          UPDATE User
          SET email={email}, name = {name}, surname = {surname}
          WHERE id = {id} AND password = {password}
                        """).on("email" -> pEmail, "password" -> pPassword, "name" -> pName,
          "surname" -> pSurname)
      if (pNewPassword.isEmpty)
        query1.execute
      else
        query2.execute
    }

  }

  def loginUser(pEmail: String, pPassword: String): Option[User] = {
    DB.withTransaction { implicit c =>
      val query = SQL( """
          SELECT * FROM User
          WHERE email = {email} AND password = {password}
                       """).on("email" -> pEmail, "password" -> pPassword)
      try {
        val result = query.executeQuery.as(parser *)
        result match {
          case Nil => throw new UserCredentialsException()
          case x :: xs => {
            SQL(
              """
                |REPLACE INTO LoginCheck SET userid = {uid}, loginTime = NOW(), isMailSent = isMailSent
              """.stripMargin).on("uid"->x.id).executeUpdate()
            Some(x)
          }

        }
      }
      catch {
        case mysqlException: jdbc4.MySQLIntegrityConstraintViolationException => {
          throw new Exception("{'error':'An error occured with error code :" + mysqlException.getErrorCode + ". Please contact us with error code '}")
        }
        case uce: UserCredentialsException => {
          throw new Exception("{'error':'Username/password not correct'}")
        }
        case ex: Throwable => {
          ex.printStackTrace()
          throw new Exception("{'error':'An unknown error occured. Please contact us!'}")
        }
      }

    }
  }

  def getUserById(pId: Long): User = {
    DB.withConnection { implicit c =>
      val query = SQL( """
          Select * FROM User
          WHERE {id} = id
                       """).on("id" -> pId)

      val queryResult = query.executeQuery()

      queryResult.as(parser *).head

    }
  }

  def killUser(pId: Long): Boolean = {
    DB.withConnection{ implicit  c =>
      val query = SQL(
        """
          |UPDATE User
          |SET isDead = {isDead}
          |WHERE id ={id}
        """.stripMargin)
      .on("id"->pId, "isDead"->true)

      query.execute()
    }
  }


}

class UserCredentialsException() extends Exception