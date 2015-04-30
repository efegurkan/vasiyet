package controllers

import model.Memorial
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, BodyParsers, Controller}
import utility.AuthAction

import scala.util.Try

object MemorialController extends Controller {

  def showPage() = AuthAction { request =>
    Ok(views.html.generic.memorial(0, false))
  }

  def showMemorial(id: Long) = Action { request =>
    //check if exists, if true
    if (Memorial.isExists(id)) {
      Ok(views.html.generic.memorial(id, true))
    }
    //else go not found page
    else {
      NotFound
    }
  }

  def pagination() = Action(BodyParsers.parse.json) { request =>
    try {

      val pagenumvalid: Boolean = Try((request.body \ "pagenum").as[String].toInt.ensuring(i => i > 0)).isSuccess
      val memorialidValid: Boolean = Try((request.body \ "memorialId").as[String].toLong.ensuring(i => i > 0)).isSuccess
      val pagenum = if (pagenumvalid) {
        (request.body \ "pagenum").as[String].toInt
      } else 1

      val memorialid = if (memorialidValid) {
        (request.body \ "memorialId").as[String].toLong
      } else throw new Exception("Memorial Id is not valid")

      //check if authenticated or not
      if (request.session.isEmpty) {//not authenticated
        //get public
        val json = Memorial.getGenericMemorial(memorialid, pagenum)
        Ok(json)
      }
      else {
        //todo get all if authenticated

        BadRequest("dsa")
      }
    }
    catch {
      case ex: Exception => {
        Logger.error("PostController Error")
        Logger.error(ex.getMessage)
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }


  }
}
