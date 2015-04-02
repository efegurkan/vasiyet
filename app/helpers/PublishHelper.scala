package helpers

import datalayer.UserDBHelper
import model.User

object PublishHelper {

  def publishMemorial(merhumId: Long): String = {

    val merhum = UserDBHelper.getUserById(merhumId)
    if (checkState(merhum)) {//RIP
      //merhum.memorial
    } else {
      setState
      MemorialHelper.createMemorial(merhum)
    }
    "return url"
  }


  def checkState(merhum: User): Boolean = {

    //todo implementation
    false
  }

  def setState = {
    //todo implementation
  }


}
