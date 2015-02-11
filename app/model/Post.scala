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
                date: DateTime)

object Post extends JSONConvertable[Post] {
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

    if (idValid && titleValid && contentValid) {
      new Post(id.get, title, content, None, 0, new DateTime())
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
        val insertedId = PostDBHelper.createPost(data.title, data.content, data.filepath, data.sender, data.date)
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