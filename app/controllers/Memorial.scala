package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utility.AuthAction

object Memorial extends Controller{

  def showPage() = AuthAction {request =>
    Ok(views.html.logged.memorial())
  }

  def pagination() = Action{ request =>
    Ok(Json.obj())
  }
}
