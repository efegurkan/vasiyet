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
      val memorialid = MemorialDBHelper.createMemorial(merhum)


      //lock members posts
      PostLocker.lockPosts(merhum)
      //retrieve member's contacts
      //edit contact email <==> memorial lookup table
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

      MemorialDBHelper.generateLookupTable(memorialId, contacts)
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
        Logger.error("Exception on populateLookupTable")
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
