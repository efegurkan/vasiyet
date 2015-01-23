package controllers

import play.api.mvc._
import utility.AuthAction

object Contact extends Controller{

  def showPage = AuthAction{request =>
    //TODO pull Contacts and Groups
    Ok(views.html.logged.contacts( List[model.Contact]() , List[model.Group]() ) )

  }
}
