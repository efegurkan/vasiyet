package controllers

import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import play.api.mvc.Action
import play.api.data._
import play.api.data.Forms._
import model.{User,RegisterForm}

object Register extends Controller {

  val registerForm = Form(
      tuple(
          "Email"-> email,
          "password"-> nonEmptyText,
          "name"-> text,
          "surname"->text
         
          )
      )

  implicit val regForm : Reads[RegisterForm] = (
    (JsPath \ "email").read[String] and
    (JsPath \ "password").read[String] and
    (JsPath \ "name").read[String] and
    (JsPath \ "surname").read[String]

    )(RegisterForm.apply _)
      
  def renderRegister = Action{implicit request =>
  	Logger.warn("renderRegister")
  	Ok(views.html.register("Register"))
  }

  def registerJson = Action(BodyParsers.parse.json){implicit request =>
    val formData = request.body.validate[RegisterForm]

    formData.fold(
      errors => {
        BadRequest(Json.obj("Status"->"KO","message"->JsError.toFlatJson(errors)))
      },
      data => {
        try {
          val registerObj = User.registerJson(data)
          Logger.warn("register success")
          Ok(Json.obj("Status"->"OK", "message"->"/home")).withSession("LoggedUser" -> registerObj.get.id.toString)
        }
        catch{
          case e: Exception =>{
            BadRequest(Json.obj("Status"->"KO","message"->e.getMessage))

          }
        }
      }
    )
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