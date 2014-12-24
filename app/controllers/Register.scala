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
  	Ok(views.html.register("Register"))
  }
  
  def register = Action{implicit request =>
    registerForm.bindFromRequest.fold(
         errors => {
           Logger.warn("In Registor errors")
           Logger.error("errors"+errors.errorsAsJson)
      BadRequest(views.html.register( "error"+errors.errorsAsJson))
    },
    form => {
      val (email: String, password: String, name: String, surname : String)=form
      val userObj = User.register(email, password,name,surname)

      userObj match {
        case None => {Logger.warn("Register.register: Registry Unsuccessful")
          Logger.warn(userObj.toString)
          BadRequest(views.html.register("Something went wrong during registry"))
        }
        case x => {
          Logger.info("Register.register: Success ")
          Logger.info(userObj.toString)
          Redirect(routes.Application.index())
        }
      }
    }
  )
  }
}