package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc._
import play.api._
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
           Logger.error("olmadi")
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
      Ok(views.html.login())
    }
  }
}
