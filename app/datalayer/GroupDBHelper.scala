package datalayer

import com.mysql.jdbc.exceptions.jdbc4
import play.api.Logger
import play.api.db.DB
import play.api.Play.current
import anorm.{SqlParser, ~, RowParser, SQL}
import anorm.SqlParser._
import model.{Group,Contact}

object GroupDBHelper extends DBHelper[Group]{


  def parser : RowParser[Group] = {
    get[Option[Long]]("id") ~
      get[String]("name")map{
      case id ~ name => Group(id,name)
    }
  }

  def createGroup(pGroupName: String): Boolean ={
    DB.withConnection{implicit c=>
      val query =SQL(
        """ INSERT INTO Group
            VALUES( NULL, {name} )
        """).on("name"->pGroupName)

      try{
        query.execute()
      }catch {
        case ex : jdbc4.MySQLIntegrityConstraintViolationException=> {Logger.error(ex.getErrorCode.toString)
            throw new Exception("{'error':'Please contact us with this error code:'"+ex.getErrorCode + "}")
        }
        case e : Throwable =>{
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          e.printStackTrace()
          false
        }
      }

    }
  }

  def deleteGroup(pId : Option[Long]): Boolean ={
    DB.withConnection{implicit c=>
      val query = SQL(
        """
          |DELETE FROM Group
          |Where id = {id}
        """.stripMargin

      ).on("id"-> pId)

      try{
        query.execute()
      }catch {
        case ex : jdbc4.MySQLIntegrityConstraintViolationException=> {Logger.error(ex.getErrorCode.toString)
          throw new Exception("{'error':'Please contact us with this error code:'"+ex.getErrorCode + "}")
        }
        case e : Throwable =>{
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          e.printStackTrace()
          false
        }
      }

    }
  }

  def updateGroup(pId : Option[Long], name: String): Boolean = {
    DB.withConnection{implicit c=>
      val query = SQL(
        """
          |UPDATE Group
          |SET name = {name}
          |WHERE id = {id}
        """.stripMargin

      ).on("id"->pId, "name"->name)

      try{
        query.execute()
      }catch {
        case ex : jdbc4.MySQLIntegrityConstraintViolationException=> {Logger.error(ex.getErrorCode.toString)
          throw new Exception("{'error':'Please contact us with this error code:'"+ex.getErrorCode + "}")
        }
        case e : Throwable =>{
          throw new Exception("{'error':'Unknown error occured. Please contact us!'}")
          e.printStackTrace()
          false
        }
      }
    }
  }

  def getGroupById(pGroupId : Option[Long]) : Option[Group] = {
    DB.withConnection{implicit c=>
      val query = SQL(
        """
          |SELECT * FROM Group
          |Where id = {groupid}
        """.stripMargin).on("groupid"->pGroupId)
      try{

        val queryResult = query.executeQuery()

        val ret : Option[Group] = queryResult.as(parser *).toList.headOption// take group or None

        val result: Option[Group] = ret.map {// map result or None
          r =>
            val contact = ContactDBHelper.getGroupContacts(r.id).getOrElse(List.empty[Contact])// get contacts, else empty List

            if(contact.nonEmpty) {
              r.copy(members = contact)// create new object with members
            } else {
              r// return object with empty members
            }
        }
        result


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

  //TODO rework on this
  def getGroupsOfUser(pUserId:Option[Long]):List[Option[Group]] ={
    DB.withConnection{implicit c=>
      val query = SQL(
        """
          |SELECT groupId FROM Group
          |WHERE id
          |IN(SELECT groupId FROM GroupContactLookup WHERE userId={userid})
        """.stripMargin).on("userid"-> pUserId)
      
      val queryResult = query.executeQuery()
      val groupList : List[Long] = queryResult.parse(SqlParser.long("groupId").*).toList

      val groups : List[Option[Group]] = groupList.map(a => getGroupById(Option(a)))

      groups
    }


  }
}
