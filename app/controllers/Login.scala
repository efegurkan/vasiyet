package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Action
import play.api.data._
import play.api.data.Forms._
import model.User

object Login extends Controller {
  
  val userForm = Form(
      tuple(
          "pEmail"-> email,
          "password"-> nonEmptyText
          )
      )

   def login = Action{ implicit request =>
    userForm.bindFromRequest.fold(
         errors => {
           Logger.error("errors"+errors.errorsAsJson)
      BadRequest(views.html.login("error"+errors.errorsAsJson))
    },
    form => {
      val (email: String, password: String)=form
      val userObj = User.login(email, password)
      Logger.error("im here")
      Redirect(routes.Application.index).flashing("success" -> "Contact saved!")
    }
  )
  }
  
   def renderLogin = Action { implicit request =>
     {
        Logger.error("render")
      Ok(views.html.login())
    }
  }
}
