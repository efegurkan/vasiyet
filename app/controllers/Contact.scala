package controllers

import datalayer.{GroupDBHelper, ContactDBHelper}
import play.api.Logger
import play.api.mvc._
import utility.AuthAction

object Contact extends Controller{

  def showPage = AuthAction{request =>
    val id = request.session.get("LoggedUser")
    //TODO exception cases
    val contacts = ContactDBHelper.getContactsByUserId(id.get.toLong)
    val groups =GroupDBHelper.getGroupsOfUser(Some(id.get.toLong))
    Ok(views.html.logged.contacts( contacts , groups ) )

  }
}
