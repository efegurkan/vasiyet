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
           Logger.error("Login.login: "+errors.errorsAsJson)
      BadRequest(views.html.login("error"+errors.errorsAsJson))
    },
    form => {
      val (email: String, password: String)=form
      val userObj = User.login(email, password)

      userObj match {
        case None =>
          Logger.warn("Login.login: Incorrect credentials")
          Logger.warn(userObj.toString)
          BadRequest(views.html.login("Incorrect E-mail or password!"))
        case x =>
          Logger.info("Login.login: Success ")
          Logger.info(userObj.toString)
          Redirect(routes.Application.index()).flashing("success" -> "Contact saved!")
      }
    }
  )
  }
  
   def renderLogin = Action { implicit request =>
     {
        Logger.info("Login.renderLogin: Success")
      Ok(views.html.login("Vasiyet Login"))
    }
  }
}
