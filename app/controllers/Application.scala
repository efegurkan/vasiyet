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
    Ok(views.html.logged.contacts())
  }

  def contacts() = AuthAction{ request =>
    Ok(views.html.logged.contacts())
  }


  def editcontact(id: Long) = AuthAction{request =>
    Ok(views.html.logged.editcontact(id,"Edit"))}

  def editgroup(id: Long) = AuthAction{request =>
    Ok(views.html.logged.editgroup(id, "Edit"))
  }

  def addgroup() = AuthAction{request =>
    Ok(views.html.logged.editgroup(0,"Add"))
  }

  def addcontact() = AuthAction{request =>
    Ok(views.html.logged.editcontact(0, "Add"))
  }
}