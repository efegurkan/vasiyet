package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utility.AuthAction

object Memorial extends Controller{

  def showPage() = AuthAction {request =>
    Ok(views.html.generic.memorial(0,false))
  }

  def showPubPage(id: Long) = Action{request =>
    //todo check if exists, if true
    Ok(views.html.generic.memorial(id,true))
    //todo else go not found page
  }

  def pagination() = Action{ request =>
    Ok(Json.obj())
  }
}
