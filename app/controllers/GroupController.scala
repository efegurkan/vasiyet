package controllers

import model.{Contact, EditGroupForm}
import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._
import play.api.mvc.Controller

object GroupController  extends Controller{
//  implicit val editGroupForm: Reads[EditGroupForm] = (
//    (JsPath \ "id").read[Option[Long]] and
//      (JsPath \ "name").read[String](Reads.minLength[String](1))
//    )(EditGroupForm.apply _)


}
