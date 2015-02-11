package datalayer

import model.Post
import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime

object PostDBHelper extends DBHelper[Post] {
  
  def parser : RowParser[Post] = {
    get[Long]("id") ~
    get[String]("title") ~ 
    get[String]("content") ~
    get[Option[String]]("filepath")~
    get[Long]("sender") ~
    get[DateTime]("date") map{
      case id~title~content~filepath~sender~date => Post(id,title,content,filepath,sender,date)
    }
  }
  
  def getPostsBySenderId( pSenderId : Long ) : List[Post] = {
    DB.withConnection{  implicit c => 
      val query = SQL("""
          Select * from Post
          Where sender = {id}
          """
          ).on("id" -> pSenderId)
        val result = query.executeQuery()
        val posts = result.as(parser * ).toList
        posts
    }
  }

  //todo visibility
  def createPost(title: String,
                 content: String,
                 filepath: Option[String],
                 sender: Long,
                 date: DateTime) : Long = {DB.withConnection{ implicit  c=>

    val query = SQL("INSERT INTO vasiyet.Post VALUES(Null, title, content, filepath,sender,date)").on("title"->title, "content"->content, "filepath"->filepath,"sender"->sender,"date"->date)
    val insertedId = query.executeInsert()
    insertedId.getOrElse(0)
    }
  }

}