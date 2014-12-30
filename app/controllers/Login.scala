package controllers

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.mvc.Action
import play.api.data._
import play.api.data.Forms._
import model.{User,LoginForm}



object Login extends Controller {

  val userForm = Form(
      tuple(
          "pEmail"-> email,
          "password"-> nonEmptyText
          )
      )

  implicit val userLogin: Reads[LoginForm] =(
    (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )(LoginForm.apply _)

  def loginJson = Action(BodyParsers.parse.json) { implicit  request =>
    val formData = request.body.validate[LoginForm]

    formData.fold(
    errors => {
      Logger.warn("loginJsonerror")
      BadRequest(Json.obj("Status"-> "KO","message"->JsError.toFlatJson(errors)))
    },
    data =>{
      val userObj = User.loginJson(data)
      Logger.warn("loginjson success")
      Ok("/home").withSession("LoggedUser"->userObj.get.id.toString)
    }

    )
  }

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
          Redirect(routes.Application.home).withSession("LoggedUser"->userObj.get.id.toString)
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

  def logout = Action{ implicit request =>{
      Redirect(routes.Login.renderLogin()).withNewSession
    }
  }
}
