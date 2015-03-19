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

  def getPostsByPage(userId: Long, pageNum: Int): (List[Post],Long,Long) = {
    DB.withConnection { implicit c =>

      val pageElementCount = 15
      val convertedPageNum = pageNum - 1
      //      val start = pageElementCount* convertedPageNum
      //      val end = start + pageElementCount

      val pageQuery = SQL(
        """
          |SELECT COUNT(id)
          |FROM Post
        """.stripMargin)

      val totalElementCount = pageQuery.executeQuery().as(scalar[Long].single)

      val maxPageNumber = math.ceil(totalElementCount.toDouble / pageElementCount.toDouble)
      println(maxPageNumber)

      val start = if (maxPageNumber < pageNum) {
        pageElementCount * (maxPageNumber - 1)
      } else {
        pageElementCount * convertedPageNum
      }
      println(start)

      val end = if (maxPageNumber < pageNum) {
        (maxPageNumber) * pageElementCount
      } else {
        start + pageElementCount
      }
      println(end)

      val query = SQL(
        """
          |SELECT Post.id, title, content,filepath, sender, date, groupId
          |FROM Post
          |INNER JOIN PostVisibilityLookup
          |WHERE sender = {id} AND PostVisibilityLookup.postId = Post.id
          |ORDER BY date DESC
          |LIMIT {start},{end}
        """.stripMargin).on("id" -> userId, "start" -> start.toInt, "end" -> end.toInt)
      val result = query.executeQuery()
      val posts = result.as(parser *).toList
      (posts,(end/pageElementCount).toLong,maxPageNumber.toLong)

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

  //todo missing filepath, sender etc
  def editPost(id: Long,
               title: String,
               content: String,
               filePath: Option[String],
               date: DateTime,
               visibility: Option[Long]) = {
    DB.withTransaction { implicit c =>
      val query1 = SQL(
        """
          |UPDATE vasiyet.Post
          |SET title = {title}, content = {content}, date = {date}
          |WHERE vasiyet.Post.id = {id}
        """.stripMargin).on("id" -> id, "title" -> title, "content" -> content, "date" -> date)
      query1.executeUpdate();

      val query2 = SQL(
        """
          |UPDATE vasiyet.PostVisibilityLookup
          |SET groupId = {groupId}
          |WHERE vasiyet.PostVisibilityLookup.postId= {postId}
        """.stripMargin).on("postId" -> id, "groupId" -> visibility.get)
      query2.executeUpdate()

      true
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