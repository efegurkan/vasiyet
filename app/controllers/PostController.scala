package controllers

import controllers.Application._
import datalayer.GroupDBHelper
import model.Post
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.BodyParsers
import play.mvc._
import utility.AuthAction

import scala.util.Try

object PostController extends Controller {

  def showPage = AuthAction { request =>
    try {
      val loggedUser = request.session.get("LoggedUser").get.toLong
      val posts = Post.getPosts(loggedUser)
      //todo move it to group
      val groups = GroupDBHelper.getGroupsOfUser(Some(loggedUser))

      //todo get groups and pass
      Ok(views.html.logged.posts(loggedUser, posts.reverse, groups))
    }
    catch {
      case ex: Exception => {
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }
  }

  def addPost = AuthAction(BodyParsers.parse.json) { request =>
    try {
      //todo senderdata is not in json
      val post = Post.fromJSON(request.body)

      val ret = Post.editPost(post, request.session.get("LoggedUser").get.toLong)

      if (ret._1) {
        Ok(Json.obj("Status" -> "OK", "message" -> "Post saved successfully", "postId" -> ret._2))
      }
      else {
        throw new Exception("Post save failed!")
      }
    }
    catch {
      case ex: Exception => {
        Logger.error("PostController Error")
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }

    }

  }

  def deletePost() = AuthAction(BodyParsers.parse.json) { request =>

    try {
      val idValid: Boolean = Try((request.body \ "postId").as[String].toLong.ensuring(i => i > 0)).isSuccess
      val id = Try((request.body \ "postId").as[String].toLong).get
      val isItDeleted = Post.deletePost(id)

      if (isItDeleted) {
        Ok(Json.obj("Status" -> "OK", "message" -> "Post deleted successfully"))
      }
      else {
        throw new Exception("Post deletion failed!")
      }
    }
    catch {
      case ex: Exception => {
        Logger.error("PostController delete Error")
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }
  }

  def getPosts() = AuthAction(BodyParsers.parse.json){request =>

    try {
      val idvalid: Boolean = Try((request.body \ "loggedUser").as[String].toLong.ensuring(i=> i>0)).isSuccess
      val id = Try((request.body \ "loggedUser").as[String].toLong).get
      val posts = Post.getPostsJson(id)
      Ok(posts)
    }
    catch {
      case ex: Exception => {
        Logger.error("Postcontroller get posts")
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status"->"KO", "message"-> ex.getMessage))
      }
    }

  }
}