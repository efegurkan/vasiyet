package controllers

import model._
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

  def addcontact() = AuthAction{request =>
    val empty = new model.Contact(new Some[Long](0),"","","")
    Ok(views.html.logged.editcontact(empty,"Add"))
  }

  def editcontact(id: Long) = AuthAction{request =>
    //TODO pull contact name from DB
    val nonEmpty = new model.Contact(new Some[Long](0),"This","is a","test")
    Ok(views.html.logged.editcontact(nonEmpty, "Edit"))}

  def addgroup() = AuthAction{request =>
    val empty = new Group(new Some[Long](0), "");
    Ok(views.html.logged.editgroup(empty, "Add"))
  }

  def editgroup(id: Long) = AuthAction{request =>
    //TODO pull group data from DB
    val nonEmpty = new Group(new Some[Long](0),"")
    Ok(views.html.logged.editgroup(nonEmpty, "Edit"))
  }
}