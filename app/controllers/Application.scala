package controllers

import datalayer.{GroupDBHelper, ContactDBHelper}
import model._
import play.api.Logger
import play.api.mvc._
import utility.AuthAction


object Application extends Controller{

  def index = Action{request =>
    Redirect("/login")
  }

  def home = AuthAction{request =>
    Redirect("/contacts")
//    Ok(views.html.logged.contacts())
  }

/*
  def contacts() = AuthAction{ request =>
    Ok(views.html.logged.contacts())

  }
*/




  def addgroup() = AuthAction{request =>
    val empty = new Group(new Some[Long](0), "");
    Ok(views.html.logged.editgroup(empty, "Add"))
  }

  def editgroup(id: Long) = AuthAction{request =>
    //TODO inform user about Redirect
    Logger.warn(id.toString)
    val group = GroupDBHelper.getGroupById(Option(id))
    if(!group.isDefined){
      Redirect("/")
    }
    else
//    val nonEmpty = new Group(new Some[Long](0),"")
    Ok(views.html.logged.editgroup(group.get, "Edit"))
  }
}