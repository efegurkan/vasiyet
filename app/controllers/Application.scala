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


  def test = AuthAction{request =>

    val uid = request.session.get("userid").get.toLong
    println("Uid:" + uid)
    val user = UserDBHelper.getUserById(uid)
    println("user:"+ user)
    MemorialHelper.createMemorial(user)
    Ok("/")
  }

  def mailtest = AuthAction{request =>

    MailNotificationHelper.testMailer
    Ok("Check email")
  }
}