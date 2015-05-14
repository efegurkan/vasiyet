package controllers

import datalayer.{UserDBHelper, GroupDBHelper, ContactDBHelper}
import helpers.{MailNotificationHelper, MemorialHelper}
import model._
import play.api.Logger
import play.api.mvc._
import utility.AuthAction


object Application extends Controller{

  def index = Action{request =>
    Redirect("/login")
  }

  def home = AuthAction{request =>
    Redirect("/posts")
//    Ok(views.html.logged.contacts())
  }

/*
  def contacts() = AuthAction{ request =>
    Ok(views.html.logged.contacts())

  }
*/
}