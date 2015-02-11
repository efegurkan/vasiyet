package controllers

import controllers.Application._
import play.api.mvc.Action
import play.mvc._
import utility.AuthAction

object PostController extends Controller {

  def showPage = AuthAction{ request =>
    Ok(views.html.logged.posts())
  }

}
