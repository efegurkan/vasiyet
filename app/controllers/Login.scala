package controllers

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.mvc.Action
import play.api.data._
import play.api.data.Forms._
import model.{User, LoginForm}


object Login extends Controller {

  implicit val userLogin: Reads[LoginForm] = (
    (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )(LoginForm.apply _)

  def loginJson = Action(BodyParsers.parse.json) { implicit request =>
    val formData = request.body.validate[LoginForm]

    formData.fold(
      errors => {
        Logger.warn("loginJsonerror")
        BadRequest(Json.obj("Status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      data => {
        try {
          val userObj = User.loginJson(data)
          Logger.warn("loginjson success")
          Ok("/home").withSession("LoggedUser" -> userObj.get.id.toString)
        }
        catch{
          case e : Exception =>{
            Logger.warn(e.getMessage)
            BadRequest(e.getMessage)
          }
        }
      }

    )
  }

  def renderLogin = Action { implicit request => {
    if (request.session.get("LoggedUser").nonEmpty) {
      Redirect(routes.Application.home())
    } else {
      Logger.info("Login.renderLogin: Success")
      Ok(views.html.login("Vasiyet Login"))
    }
  }
  }

  def logout = Action { implicit request => {
    if(request.session.get("LoggedUser").nonEmpty){
      Redirect(routes.Login.renderLogin()).withNewSession
    }
    else
    {
      Redirect(routes.Login.renderLogin())
    }
  }
  }
}
