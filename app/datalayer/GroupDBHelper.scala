package datalayer

import anorm.SqlParser._
import anorm.{RowParser, SQL, SqlParser, ~}
import com.mysql.jdbc.exceptions.jdbc4
import model.{Contact, Group}
import play.api.Logger
import play.api.Play.current
import play.api.db.DB

object GroupDBHelper extends DBHelper[Group] {

  def parser: RowParser[Group] = {
    get[Option[Long]]("id") ~
      get[String]("name") map {
      case id ~ name => Group(id, name)
    }
  }

  //todo auth check
  def createGroup(pGroupName: String, userId: Long): Long = {
    try {
      DB.withTransaction { implicit c =>
        val insertedId = SQL("INSERT INTO vasiyet.Group VALUES( NULL, {name} )").on("name" -> pGroupName).executeInsert()

        val query2 =SQL("INSERT INTO vasiyet.UserGroupLookup VALUES(NULL, {user}, {inserted} )").on("inserted" -> insertedId, "user" -> userId)
        val insGroupId = query2.executeInsert()
        insGroupId.getOrElse(0)
      }
    } catch {
      case ex: jdbc4.MySQLIntegrityConstraintViolationException => {
        Logger.error(ex.getErrorCode.toString)
        throw new Exception("{'error':'Please contact us with this error code:'" + ex.getErrorCode + "}")
      }
      case e: Throwable => {
        e.printStackTrace()
        throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
      }
    }
  }
  //todo auth check
  def deleteGroup(pId: Long,sessionid: Long): Boolean = {
    DB.withTransaction { implicit c =>
      val query = SQL(
        """DELETE FROM vasiyet.Group
          |Where id = {id}
          |AND EXISTS (SELECT 1 FROM UserGroupLookup WHERE userId = {userid} AND GroupId = {id})""".stripMargin)
        .on("id" -> pId,"userid"->sessionid)

      val query2 = SQL(
        """
          |DELETE FROM PostVisibilityLookup
          |WHERE groupId = {id}
        """.stripMargin).on("id"-> pId)
      try {
        val res1 = query.executeUpdate()
        val res2 = query2.executeUpdate()
        (res1!=0 &&  res2!=0)

      } catch {
        case ex: jdbc4.MySQLIntegrityConstraintViolationException => {
          Logger.error(ex.getErrorCode.toString)
          throw new Exception("{'error':'Please contact us with this error code:'" + ex.getErrorCode + "}")
        }
        case e: Throwable => {
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          e.printStackTrace()
          false
        }
      }

    }
  }

  //todo auth check
  def updateGroup(pId: Option[Long], name: String): Boolean = {
    DB.withConnection { implicit c =>
      val query = SQL("UPDATE vasiyet.Group SET name = {name} WHERE id = {id}").on("id" -> pId, "name" -> name)

      try {
        query.executeUpdate() != 0
      } catch {
        case ex: jdbc4.MySQLIntegrityConstraintViolationException => {
          Logger.error(ex.getErrorCode.toString)
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

  //auth check
  def getGroupByIdWithUser(pGroupId: Long,sessionId: Long): Option[Group] = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |SELECT * FROM vasiyet.Group
          |Where id ={groupid}
          |AND EXISTS(SELECT 1 FROM UserGroupLookup WHERE userId = {userid} AND groupId = {groupid})
          |""".stripMargin).on("groupid" -> pGroupId,"userid"->sessionId)
      try {

        val queryResult = query.executeQuery()

        val ret: Option[Group] = queryResult.as(parser *).headOption // take group or None

        val result: Option[Group] = ret.map {
          // map result or None
          r =>
            val contact = ContactDBHelper.getGroupContacts(r.id).getOrElse(List.empty[Contact]) // get contacts, else empty List

            if (contact.nonEmpty) {
              r.copy(members = contact) // create new object with members
            } else {
              r // return object with empty members
            }
        }
        result


      } catch {
        case ex: jdbc4.MySQLIntegrityConstraintViolationException => {
          Logger.error(ex.getErrorCode.toString)
          throw new Exception("{'error':'Please contact us with this error code:'" + ex.getErrorCode + "}")
        }
        case e: Throwable => {
          Logger.error(e.getMessage)
          e.printStackTrace()
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          None
        }
      }
    }
  }

  def getGroupById(pGroupId: Long): Option[Group] = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |SELECT * FROM vasiyet.Group
          |Where id ={groupid}
          |""".stripMargin).on("groupid" -> pGroupId)
      try {

        val queryResult = query.executeQuery()

        val ret: Option[Group] = queryResult.as(parser *).headOption // take group or None

        val result: Option[Group] = ret.map {
          // map result or None
          r =>
            val contact = ContactDBHelper.getGroupContacts(r.id).getOrElse(List.empty[Contact]) // get contacts, else empty List

            if (contact.nonEmpty) {
              r.copy(members = contact) // create new object with members
            } else {
              r // return object with empty members
            }
        }
        result


      } catch {
        case ex: jdbc4.MySQLIntegrityConstraintViolationException => {
          Logger.error(ex.getErrorCode.toString)
          throw new Exception("{'error':'Please contact us with this error code:'" + ex.getErrorCode + "}")
        }
        case e: Throwable => {
          Logger.error(e.getMessage)
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          e.printStackTrace()
          None
        }
      }
    }
  }

  //todo auth check
  def getGroupsOfUser(pUserId: Long): List[Group] = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
            SELECT groupId FROM UserGroupLookup WHERE userId={userid}
        """.stripMargin).on("userid" -> pUserId)

      val queryResult = query.executeQuery()
      val groupList: List[Long] = queryResult.parse(SqlParser.long("groupId").*).toList

      val groups: List[Group] = groupList.map(x => getGroupById(x)).flatMap(_.toList)

      groups
    }


  }

  //todo auth check
  def deleteMember(groupid: Long, contactid: Long, sessionid:Long): Boolean = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """DELETE FROM vasiyet.GroupContactLookup
          |WHERE groupId = {groupid} AND contactId = {contactid}
          |AND EXISTS(SELECT 1 FROM UserGroupLookup WHERE userId = {userid} AND groupId = {groupid})
          |""".stripMargin).
        on("groupid"->groupid, "contactid"->contactid,"userid"->sessionid).executeUpdate()
      query !=0
    }
  }

  //todo auth check
  def addMember(groupId: Long, contactId: Long): Boolean = {
    DB.withConnection{implicit c =>
      val query = SQL("INSERT INTO vasiyet.GroupContactLookup VALUES(NULL,{groupid}, {contactid})").
        on("groupid"->groupId,"contactid"->contactId)

      query.executeUpdate()!= 0
    }
  }
}
