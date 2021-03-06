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

  /*Auth only*/
  def showPage = AuthAction { request =>
    try {
      val loggedUser = request.session.get("userid").get.toLong
      val posts = Post.getPosts(loggedUser)
      //todo move it to group
      val groups = GroupDBHelper.getGroupsOfUser(loggedUser)

      Ok(views.html.logged.posts(loggedUser, posts.reverse, groups))
    }
    catch {
      case ex: Exception => {
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }
  }

  /*Auth only*/
  def pagination = AuthAction(BodyParsers.parse.json) { request =>
    try {

      val pagenumvalid: Boolean = Try((request.body \ "pagenum").as[String].toInt.ensuring(i => i > 0)).isSuccess
      val loggedUser = request.session.get("userid").get.toLong
      if (pagenumvalid) {
        val pagenum = Try((request.body \ "pagenum").as[String].toInt).get
        val pagePosts = Post.getAllPostsPaginated(loggedUser, pagenum)
        Ok(pagePosts)
      }
      else {
        val pagenum = 1
        val pagePosts = Post.getAllPostsPaginated(loggedUser, pagenum)
        Ok(pagePosts)
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

  /*Auth check, only add a post to your own account*/
  def addPost() = AuthAction(BodyParsers.parse.json) { request =>
    try {
      val post = Post.fromJSON(request.body)
      val loggedUser = request.session.get("userid").get.toLong
      val ret = Post.editPost(post, loggedUser)

      if (ret._1) {
        Ok(Json.obj("Status" -> "OK", "message" -> "Post saved successfully", "post" -> ret._2))
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

  /*Auth check, only delete own posts*/
  def deletePost() = AuthAction(BodyParsers.parse.json) { request =>

    try {
      val idValid: Boolean = Try((request.body \ "postId").as[String].toLong.ensuring(i => i > 0)).isSuccess
      val loggedUser = request.session.get("userid").get.toLong
      val id = Try((request.body \ "postId").as[String].toLong).get
      val isItDeleted = Post.deletePost(id, loggedUser)

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

  /*Auth check, get only your posts*/
  def getPosts = AuthAction { request =>

    try {
      val id = request.session.get("userid").get.toLong
      val posts = Post.getPostsJson(id)
      Ok(posts)
    }
    catch {
      case ex: Exception => {
        Logger.error("Postcontroller get posts")
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }

  }
}