package controllers

import controllers.Application._
import model.Post
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.BodyParsers
import play.mvc._
import utility.AuthAction

object PostController extends Controller {

  def showPage = AuthAction { request =>
    try {
      val posts = Post.getPosts(request.session.get("LoggedUser").get.toLong)

      //todo get groups and pass
      Ok(views.html.logged.posts(posts, List.empty))
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

      if (ret._1){
        Ok(Json.obj("Status"->"OK","message"->"Post saved successfully","postId"->ret._2))
      }
      else{
        throw new Exception("Post save failed!")
      }
    }
    catch {
      case ex:Exception => {
        Logger.error("PostController Error")
        BadRequest(Json.obj("Status"->"KO","message"->ex.getMessage))
      }

    }

  }

}
