package helpers

import datalayer.{ContactDBHelper, MemorialDBHelper}
import model.User
import play.api.Logger

object MemorialHelper {

  def getMemorialUrl(user:User):Long = {
    //url/memorial/id
    //this returns id part as string
    MemorialDBHelper.getMemorialId(user)
  }

  def createMemorial(merhum: User): Long = {
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

      MemorialDBHelper.setMemorialPublished(merhum.id)
      //return id
      memorialid
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
        Logger.error("Exception on createMemorial")
        throw new Exception("Memorial creation failed")
      }
    }
  }

  def populateLookupTable(merhum: User, memorialId:Long) = {
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

  def getMemorialContactEmails(memorialid:Long): List[String] = {
    try{
      val contacts = MemorialDBHelper.getMemorialContactEmails(memorialid)
      contacts
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
        Logger.error("Exception on getMemorialContacts")
        throw new Exception("memorial contacts couldn't retrieved.")
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
