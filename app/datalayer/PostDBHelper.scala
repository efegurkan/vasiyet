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
      get[DateTime]("date") ~
      get[Long]("groupId") map {
      case id ~ title ~ content ~ filepath ~ sender ~ date ~ visibility => Post(id, title, content, filepath, sender, date, visibility)
    }
  }

  def getPostById(postId: Long): Post = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |Select Post.id, title, content, filepath, sender, date, groupId FROM Post
          |INNER JOIN PostVisibilityLookup
          |Where Post.id = {postid} AND PostVisibilityLookup.postId = Post.id
        """.stripMargin).on("postid" -> postId)

      val result = query.executeQuery()
      val post = result.as(parser *).head
      post
    }
  }


  def getPostsBySenderId(pSenderId: Long): List[Post] = {
    DB.withConnection { implicit c =>
      val query = SQL( """
                         |Select Post.id, title, content, filepath, sender, date, groupId FROM Post
                         |INNER JOIN PostVisibilityLookup
                         |Where sender = {id} AND PostVisibilityLookup.postId = Post.id
                       """
        .stripMargin).on("id" -> pSenderId)
      val result = query.executeQuery()
      val posts = result.as(parser *).toList
      posts
    }
  }

  def createPost(title: String,
                 content: String,
                 filepath: Option[String],
                 sender: Long,
                 date: DateTime,
                 visibility: Option[Long]): Long = {
    DB.withTransaction { implicit c =>
      val query1 = SQL(
        """
          |INSERT INTO vasiyet.Post (id, title, content, filepath, sender, date)
          |VALUES                 (Null, {title}, {content},  {filepath}  ,{sender} , {date})
          |
        """.stripMargin).on("title" -> title, "content" -> content, "filepath" -> filepath.getOrElse(null), "sender" -> sender, "date" -> date)
      val insertedId: Option[Long] = query1.executeInsert()
      val query2 = SQL(
        """
          |INSERT INTO vasiyet.PostVisibilityLookup(id, postId, groupId)
          |VALUES (Null, {postId}, {groupId})
        """.stripMargin).on("postId" -> insertedId.get, "groupId" -> visibility.get)
      println(insertedId.get)
      println(visibility.get)
      val lookupdone: Option[Long] = query2.executeInsert()

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