package datalayer

import model.Post
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime

class PostDBHelper extends DBHelper[Post] {
  
  def parser : RowParser[Post] = {
    get[Option[Long]]("id") ~ 
    get[String]("title") ~ 
    get[String]("content") ~
    get[Option[String]]("filepath")~
    get[Long]("sender") ~
    get[DateTime]("date") map{
      case id~title~content~filepath~sender~date => Post(id,title,content,filepath,sender,date)
    }
  }
  
  def getPostsById( pId : Long ) : List[Post] = {
    DB.withConnection{  implicit c => 
      val query = SQL("""
          Select * from Post
          Where sender = {id}
          """
          ).on("id" -> pId)
        val result = query.executeQuery()
        val posts = result.as(parser * ).toList
        posts
    }
  }

}