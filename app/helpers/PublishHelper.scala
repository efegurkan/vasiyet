package helpers

import datalayer.UserDBHelper
import model.User

object PublishHelper {

  def publishMemorial(merhumId: Long): String = {

    val merhum = UserDBHelper.getUserById(merhumId)
    if (merhum.isdead) {//RIP
      //merhum.memorial
    } else {
      setState(merhum)
      MemorialHelper.createMemorial(merhum)
    }
    "return url"
  }


  def setState(merhum: User) = {
    User.kill(merhum)
  }


}
