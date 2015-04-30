package model

import datalayer.MemorialDBHelper
import play.api.libs.json.{Json, JsValue}

case class Memorial(id: Long,
                     owner: Long,
                     ownerName: String,
                     ownerSurname: String)

object Memorial extends JSONConvertable[Memorial]{
  override def toJSON(t: Memorial): JsValue = ???

  override def fromJSON(json: JsValue): Memorial = ???



  /*paginated*/
  /*returns public memorial to be shown everybody*/
  /*returns json*/
  def getGenericMemorial(MemorialId: Long, pagenum:Long):JsValue={
    val memorial = MemorialDBHelper.getMemorialById(MemorialId)
    val tuple = MemorialDBHelper.getPublicPostsPaginated(memorial,pagenum)
    val jsonPosts = tuple._1.map(p=> Post.toJSON(p))
    val orders = tuple._1.map(p=> p.id)

    Json.obj("posts"->jsonPosts, "orders"->orders, "activePage"->tuple._2, "maxPage"->tuple._3)
  }

  /*paginated*/
  /*returns memorial for editing purposes. It is meant to be used for the owner*/
  def getMemorial = {

  }

  /*paginated*/
  /*returns memorial which will be shown to a specific user*/
  def getUserMemorial = {

  }

  def isExists(id: Long)= {
    MemorialDBHelper.checkMemorial(id)
  }
}
