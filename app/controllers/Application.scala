package controllers

import play.api._
import play.api.mvc._
import datalayer._
import play.api.data._
import play.api.data.Forms._
import utility.AuthAction


object Application extends Controller{

  def index = Action{request =>
    Redirect("/login")
  }

//  def home = AuthAction { request =>
//     val id = request.session.get("LoggedUser")
//     Ok(views.html.home("Login success", id.get))
//
//  }

  def home = AuthAction{request =>
    Ok(views.html.contacts())
  }

  def contacts() = AuthAction{ request =>
    Ok(views.html.contacts())
  }


  def editcontact(id: Long) = AuthAction{request =>
  Ok(views.html.editcontact(id))}
}