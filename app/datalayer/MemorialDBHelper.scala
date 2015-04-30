package datalayer

import model.{Memorial, Post, Contact, User}
import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._

object MemorialDBHelper {

  def memorialParser: RowParser[Memorial] = {
    get[Long]("id") ~
      get[Long]("owner") ~
      get[String]("ownerName") ~
      get[String]("ownerSurname") map {
      case id ~ owner ~ ownerName ~ ownerSurname => Memorial(id, owner, ownerName, ownerSurname)
    }
  }

  def getMemorialById(memorialId: Long): Memorial= {
    DB.withConnection{implicit c =>
      SQL(
        """
          |SELECT * FROM Memorial WHERE id = {memorialId}
        """.stripMargin).on("memorialId"-> memorialId).executeQuery().as(memorialParser *).head
    }
  }

  def getMemorialId(user: User): Long = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |SELECT id FROM vasiyet.Memorial
          |WHERE owner = {userid}
        """.stripMargin)
        .on("userid" -> user.id)

      query.executeQuery().as(scalar[Long].single)

    }
  }


  def createMemorial(user: User): Long = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |INSERT INTO vasiyet.Memorial (id, owner, ownerName, ownerSurname)
          |VALUES (Null, {userid}, {username}, {usersurname})
        """.stripMargin)
        .on("userid" -> user.id, "username" -> user.name, "usersurname" -> user.surname)

      val inserted: Option[Long] = query.executeInsert()
      inserted.get
    }
  }

  def generateLookupTable(memorialId: Long, contacts: List[Contact]) = {
    DB.withConnection { implicit connection =>

      val indexedValues = contacts.zipWithIndex
      val rows = indexedValues.map { case (value, i) =>
        s"(Null, {val2_${i}}, {val3_${i}})"
      }.mkString(",")

      val parameters = indexedValues.flatMap { case (contact, i) =>
        Seq(
          NamedParameter(s"val2_${i}", memorialId),
          NamedParameter(s"val3_${i}", contact.email)
        )
      }

      println(parameters)
      SQL(
        """
          |INSERT INTO vasiyet.MemorialLookup(id, memorialId, contactemail) VALUES
        """.stripMargin + rows).on(parameters: _ *).executeUpdate()
    }
  }

  def checkMemorial(memorialId: Long):Boolean = {
    DB.withConnection{implicit connection =>

      SQL(
        """
          |SELECT EXISTS(SELECT 1 FROM Memorial WHERE {memorialId} = id)
        """.stripMargin).on("memorialId"->memorialId).executeQuery().as(scalar[Long].single) == 1
    }
  }

  def getPublicPostsPaginated(memorial:Memorial,pagenum:Long):(List[Post],Long,Long)={
    DB.withConnection{ implicit  c=>

      val paginationvalues = PostPagination.calculatePagination(memorial.owner,pagenum)
      val posts:List[Post] = SQL(
        """
          |SELECT Post.id, title, content, filepath, sender, date, groupId
          |FROM Post JOIN PostVisibilityLookup ON(Post.id = PostVisibilityLookup.postId)
          |WHERE groupId = 0 AND sender = {memorialOwner}
          |ORDER BY date DESC
          |LIMIT {start},{end}
        """.stripMargin).on("memorialOwner"-> memorial.owner,
          "start"->paginationvalues._1,
          "end"-> paginationvalues._2).executeQuery().as(PostDBHelper.parser *)
      //posts, activePage, maxPage
      (posts,paginationvalues._3, paginationvalues._4)
    }
  }
}
