package controllers

import model.Memorial
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, BodyParsers, Controller}
import utility.AuthAction

import scala.util.Try

object MemorialController extends Controller {

  //authonly show own memorial
  def showPage() = AuthAction { request =>
    Ok(views.html.generic.memorial(0, false))
  }

  //render page not posts
  def showMemorial(id: Long) = Action { request =>
    val isPub = request.session.isEmpty
    //check if exists, if true
    if (Memorial.isExists(id)) {
      Ok(views.html.generic.memorial(id, isPub))
    }
    //else go not found page
    else {
      NotFound
    }
  }

  //show posts
  def pagination() = Action(BodyParsers.parse.json) { request =>
    try {

      val pagenumvalid: Boolean = Try((request.body \ "pagenum").as[String].toInt.ensuring(i => i > 0)).isSuccess
      val memorialidValid: Boolean = Try((request.body \ "memorialId").as[String].toLong.ensuring(i => i >= 0)).isSuccess
      val pagenum = if (pagenumvalid) {
        (request.body \ "pagenum").as[String].toInt
      } else 1

      val memorialid = if (memorialidValid) {
        (request.body \ "memorialId").as[String].toLong
      } else throw new Exception("Memorial Id is not valid")

      //check if authenticated or not
      if (request.session.isEmpty) {
        //not authenticated
        if(memorialid == 0) {
          // auth only
          throw new Exception("You are not logged in")
        }

        //get public
        val json = Memorial.getGenericMemorial(memorialid, pagenum)
        Ok(json)
      }
      else {
        //get all or user specific memorial if session is valid
        val userid = request.session.get("userid")
        if (userid.nonEmpty) {//make sure session is not corrupt
          val sessionid = userid.get.toLong

          val json = Memorial.getMemorial(sessionid,memorialid,pagenum)
          Ok(json)
        }
        else
          throw new Exception("User Information cannot be retrieved")

      }
    }
    catch {
      case ex: Exception => {
        Logger.error("PostController Error")
        Logger.error(ex.getMessage)
        ex.printStackTrace()
        BadRequest(Json.obj("Status" -> "KO", "message" -> ex.getMessage))
      }
    }


  }
}
