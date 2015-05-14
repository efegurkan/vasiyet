package model

import datalayer.MemorialDBHelper
import play.api.libs.json.{JsValue, Json}

case class Memorial(id: Long,
                    owner: Long,
                    ownerName: String,
                    ownerSurname: String)

object Memorial extends JSONConvertable[Memorial] {
  override def toJSON(t: Memorial): JsValue = ???

  override def fromJSON(json: JsValue): Memorial = ???


  /*paginated*/
  /*returns public memorial to be shown everybody*/
  /*returns json*/
  def getGenericMemorial(MemorialId: Long, pagenum: Long): JsValue = {
    val memorial = MemorialDBHelper.getMemorialById(MemorialId)
    val tuple = MemorialDBHelper.getPublicPostsPaginated(memorial, pagenum)
    val jsonPosts = tuple._1.map(p => Post.toJSON(p))
    val orders = tuple._1.map(p => p.id)

    Json.obj("posts" -> jsonPosts, "orders" -> orders, "activePage" -> tuple._2, "maxPage" -> tuple._3)
  }

  /*paginated*/
  /*returns memorial for preview or specific to a user*/
  def getMemorial(sessionid: Long, memorialid: Long, pagenum: Int) = {
    //check user exists, get user
    val user = User.getUserById(sessionid)

    if(memorialid ==0)
    {
      //show preview memorial not created
      val tuple = MemorialDBHelper.getOwnMemorialPaginated(user, pagenum)
      val jsonPosts = tuple._1.map(p => Post.toJSON(p))
      val orders = tuple._1.map(p => p.id)

      Json.obj("posts" -> jsonPosts, "orders" -> orders, "activePage" -> tuple._2, "maxPage" -> tuple._3)
    }
    else{//memorialid != 0

      if(user.isdead){
        //somebody else logged as dead person
        println(user)
        throw new Exception("This account may be compromised. Operation not permitted")
      }
      else{//user alive
        //show other persons memorial
        //memorial exists
        if (!isExists(memorialid))
          throw new Exception("Memorial not exists.")

        val memorial = MemorialDBHelper.getMemorialById(memorialid)
        val tuple = MemorialDBHelper.getOtherMemorialPaginated(memorial, user, pagenum)
        val jsonPosts = tuple._1.map(p => Post.toJSON(p))
        val orders = tuple._1.map(p => p.id)

        Json.obj("posts" -> jsonPosts, "orders" -> orders, "activePage" -> tuple._2, "maxPage" -> tuple._3)
      }
    }
  }

  def isOwnMemorial(sessionid: Long, memorialid: Long): Boolean = {
    //if it is true, caller wants own memorial.
    if(memorialid == 0) true
    else{
    // get user, get memorialid of given user and check if they are same
    // This is the usecase of if user reaches its memorial with /memorial/$id
    val user = User.getUserById(sessionid)
    val usersMemId = MemorialDBHelper.getMemorialId(user)

    memorialid == usersMemId
    }
  }

  def isExists(id: Long)= {
    MemorialDBHelper.checkMemorial(id)
  }
}
