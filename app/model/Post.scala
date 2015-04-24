package model

import datalayer.{UserDBHelper, GroupDBHelper, PostDBHelper}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

import scala.util.Try

case class Post(id: Long,
                title: String,
                content: String,
                filepath: Option[String],
                sender: Long,
                date: DateTime,
                visibility: Long,
                isLocked: Boolean = false)

object Post extends JSONConvertable[Post] {

  def getPostsPaginated(loggedUser: Long, pageNum: Int): JsValue = {
    try {
      val tuple = PostDBHelper.getPostsByPage(loggedUser: Long, pageNum)
      val posts = tuple._1
      val jsonPosts = posts.map(p => toJSON(p))

      val orderList = posts.map(p => p.id)
      Json.obj("posts" -> jsonPosts, "orders" -> orderList, "activePage" -> tuple._2, "maxPage" -> tuple._3)
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw new Exception("Posts on the current page couldn't be retrieved!")
    }
  }


  def getPost(postId: Long): Post = {
    try {
      PostDBHelper.getPostById(postId)
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw new Exception("Post couldn't be retrieved!")
    }
  }

  def getPostsJson(userId: Long): JsValue = {
    val rawPosts = this.getPosts(userId)
    val jsonlist = rawPosts.map(p => toJSON(p))
    Json.toJson(jsonlist)
  }

  def deletePost(id: Long): Boolean = {
    try {
      PostDBHelper.deletePost(id)
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw new Exception("Post deletion could not saved on database")

    }
  }

  def lockPost(t: Post): Boolean = {
    Logger.info("Post.lockPost initiated")
    println("post:" + t)
    val ret = PostDBHelper.lockPost(t.id)
    Logger.info("LockPost on PostDBHelper worked correctly")
    ret
  }

  override def toJSON(t: Post): JsValue = {
    val group = GroupDBHelper.getGroupById(Some(t.visibility))
    val sender = UserDBHelper.getUserById(t.sender)
    val vis = if (t.visibility == 0) {
      "All"
    } else if (group.isDefined) {
      group.get.name
    }
    else throw new Exception("group is not defined")
    val json = Json.obj(
      "id" -> t.id,
      "title" -> t.title,
      "content" -> t.content,
      "filepath" -> t.filepath,
      "sender" -> (sender.name + " " + sender.surname),
      "date" -> DateTimeFormat.forPattern("dd MM yyyy").print(t.date),
      "visibility" -> vis
    )
    json
  }

  //todo visibility and other fields
  override def fromJSON(json: JsValue): Post = {
    // get data from json
    val id = Try((json \ "id").as[String].toLong.ensuring(i => i >= 0))
    val title = (json \ "title").as[String]
    val content = (json \ "content").as[String].ensuring(c => c.length <= 500)
    val groupid = Try((json \ "group").as[String].toLong.ensuring(g => g >= 0))
    //validate fields
    val idValid: Boolean = id.isSuccess
    val titleValid: Boolean = !title.isEmpty
    val contentValid: Boolean = !content.isEmpty
    val groupidValid: Boolean = groupid.isSuccess

    //todo change default values with parameters i.e. visibility 0 to groupid
    if (idValid && titleValid && contentValid && groupidValid) {
      new Post(id.get, title, content, None, 0, new DateTime(), groupid.get, false)
    }
    else {
      throw new Exception("Post Json is not valid")
    }
  }

  def getPosts(pSenderId: Long): List[Post] = {
    try {
      PostDBHelper.getPostsBySenderId(pSenderId).reverse
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw new Exception("Couldn't load posts!")
    }
  }

  def editPost(data: Post, loggedUserId: Long): (Boolean, JsValue) = {
    try {
      if (data.id == 0) {
        //Add request
        val insertedId = PostDBHelper.createPost(data.title, data.content, data.filepath, loggedUserId, data.date, Some(data.visibility))
        val post = Post.getPost(insertedId)
        (insertedId != 0, Post.toJSON(post))
      }
      else {
        PostDBHelper.editPost(data.id, data.title, data.content, data.filepath, data.date, Some(data.visibility))
        val post = Post.getPost(data.id)
        (true, Post.toJSON(post))
      }
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw new Exception("Couldn't save post!")
    }

  }
}