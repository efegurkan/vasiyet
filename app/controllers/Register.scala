package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Action
import play.api.data._
import play.api.data.Forms._
import model.User

object Register extends Controller {

  val registerForm = Form(
      tuple(
          "Email"-> email,
          "password"-> nonEmptyText,
          "name"-> text,
          "surname"->text
         
          )
      )
      
  def renderRegister = Action{implicit request =>
  	Logger.warn("renderRegister")
  	Ok(views.html.register())
  }
  
  def register = Action{implicit request =>
    registerForm.bindFromRequest.fold(
         errors => {
           Logger.warn("In Registor errors")
           Logger.error("errors"+errors.errorsAsJson)
      BadRequest(views.html.register("error"+errors.errorsAsJson))
    },
    form => {
      val (email: String, password: String, name: String, surname : String)=form
      val userObj = User.register(email, password,name,surname)
      Logger.error("registering user")
      Redirect(routes.Application.index).flashing("success" -> "Contact saved!")
    }
  )
  }
}