package helpers

import datalayer.{ContactDBHelper, MemorialDBHelper}
import model.User
import play.api.Logger

object MemorialHelper {

  def getMemorialUrl():String = {
    //todo implementation
    "return url"
  }

  def createMemorial(merhum: User): String = {
    //todo implementation
    //create memorial
    try {
      Logger.info("create memorial on memorialhelper started")
      val memorialid = MemorialDBHelper.createMemorial(merhum)
      println("memorialid:"+ memorialid)
      Logger.info("No exception on create memorial on memorialDBhelper")

      //lock members posts
      PostLocker.lockPosts(merhum)
      Logger.info("Post locker, lock posts worked")
      //edit contact email <==> memorial lookup table
      populateLookupTable(merhum,memorialid)
      Logger.info("PopulateLookupTable Worked")
      //trigger email notification
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
        Logger.error("Exception on createMemorial")
      }
    }
    "url"
  }

  def populateLookupTable(merhum: User, memorialId:Long) = {
    //todo implementation
    try {
      val contacts = ContactDBHelper.getContactsByUserId(merhum.id)
      println("contacts" + contacts)
      MemorialDBHelper.generateLookupTable(memorialId, contacts)
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
        Logger.error("Exception on populateLookupTable")
        throw new Exception("populateLookupTable did not worked")
      }
    }
  }
}

/*    _____
*   //  +  \
*  ||  RIP  |
*  ||       |
*  ||       |
* \||/\/\//\|/
*/
