package model

import datalayer.PostDBHelper
import org.joda.time.DateTime
import play.api.libs.json.JsValue

import scala.util.Try

case class Post(id: Long,
                title: String,
                content: String,
                filepath: Option[String],
                sender: Long,
                date: DateTime,
                visibility: Long)

object Post extends JSONConvertable[Post] {
  def deletePost(id: Long): Boolean = {
    try {
      PostDBHelper.deletePost(id)
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
        throw new Exception("Post deletion could not saved on database")
      }
    }
  }

  override def toJSON(t: Post): JsValue = ???

  //todo visibility and other fields
  override def fromJSON(json: JsValue): Post = {
    // get data from json
    val id = Try((json \ "id").as[String].toLong.ensuring(i => i >= 0))
    val title = (json \ "title").as[String]
    val content = (json \ "content").as[String].ensuring(c => (c.length <= 500))
    //validate fields
    val idValid: Boolean = id.isSuccess
    val titleValid: Boolean = !title.isEmpty
    val contentValid: Boolean = !content.isEmpty

    //todo change default values with parameters i.e. visibility 0 to groupid
    if (idValid && titleValid && contentValid) {
      new Post(id.get, title, content, None, 0, new DateTime(), 0)
    }
    else {
      throw new Exception("Post Json is not valid")
    }
  }

  def getPosts(pSenderId: Long): List[Post] = {
    try {
      PostDBHelper.getPostsBySenderId(pSenderId)
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
        throw new Exception("Couldn't load posts!")
      }
    }
  }

  //todo add edit, visibility
  def editPost(data: Post, loggedUserId: Long): (Boolean, Long) = {
    try {
      if (data.id == 0) {
        //Add request
        //todo sender is not provided by json!
        val insertedId = PostDBHelper.createPost(data.title, data.content, data.filepath, loggedUserId, data.date, Some(0))
        (insertedId != 0, insertedId)
      }
      else {
        //edit request
        (false, -1)
      }
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
        throw new Exception("Couldn't save post!")
      }
    }

  }
}