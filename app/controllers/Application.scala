package controllers

import play.api._
import play.api.mvc._
import datalayer._
import play.api.data._
import play.api.data.Forms._
import utility.AuthAction


object Application extends Controller{
 
//  def index = Action {request => {
//    val cHelper = new ContactDBHelper()
//    val pHelper = new PostDBHelper
//    val list = cHelper.getContactsByUserId(1)
//    val list2 = pHelper.getPostsById(1)
//    list.foreach{println}
//    println("---")
//    list2.foreach{println}
//    Ok(views.html.index("Your new application is ready."))
//  }
//
//  }

  def index = Action{request =>
    Redirect("/login")
  }

  def home = AuthAction { request =>
     val id = request.session.get("LoggedUser")
     Ok(views.html.home("Login success", id.get))

  }

}