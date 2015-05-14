package helpers

import datalayer.UserDBHelper
import model.User

object PublishHelper {

  def publishMemorial(merhumId: Long): Long = {

    val merhum = UserDBHelper.getUserById(merhumId)
    if (merhum.isdead) {//RIP
      //merhum.memorial
      MemorialHelper.getMemorialUrl(merhum)
    } else {
      setState(merhum)
      MemorialHelper.createMemorial(merhum)
    }
  }


  def setState(merhum: User) = {
    User.kill(merhum)
  }


}
