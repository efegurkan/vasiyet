package datalayer

import anorm.SqlParser._
import anorm._
import model.Post
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.DB

object PostDBHelper extends DBHelper[Post] {


  def parser: RowParser[Post] = {
    get[Long]("id") ~
      get[String]("title") ~
      get[String]("content") ~
      get[Option[String]]("filepath") ~
      get[Long]("sender") ~
      get[DateTime]("date") map {
      case id ~ title ~ content ~ filepath ~ sender ~ date => Post(id, title, content, filepath, sender, date)
    }
  }

  def getPostsBySenderId(pSenderId: Long): List[Post] = {
    DB.withConnection { implicit c =>
      val query = SQL( """
          Select * from Post
          Where sender = {id}
                       """
      ).on("id" -> pSenderId)
      val result = query.executeQuery()
      val posts = result.as(parser *).toList
      posts
    }
  }

  //todo visibility
  def createPost(title: String,
                 content: String,
                 filepath: Option[String],
                 sender: Long,
                 date: DateTime): Long = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |INSERT INTO vasiyet.Post (id, title, content, filepath, sender, date)
          |VALUES                 (Null, {title}, {content},  NULL  ,{sender} , {date})
          |
        """.stripMargin).on("title" -> title, "content" -> content, "sender" -> sender, "date" -> date)
      val insertedId = query.executeInsert()
      insertedId.getOrElse(0)
    }
  }

  def deletePost(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |DELETE FROM vasiyet.Post
          |WHERE id = {id}
        """.stripMargin).on("id" -> id)

      query.executeUpdate() != 0
    }

  }

}