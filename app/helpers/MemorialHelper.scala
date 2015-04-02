package helpers

import model.User

object MemorialHelper {

  def getMemorialUrl():String = {
    //todo implementation
    "return url"
  }

  def createMemorial(merhum:User) :String= {
    //todo implementation
    //create memorial url
    //lock members posts
    PostLocker.lockPosts(merhum)
    //retrieve member's contacts
    //edit contact email <==> memorial lookup table
    //trigger email notification
    "url"
  }

  def populateLookupTable = {
    //todo implementation
  }
}

/*    _____
*   //  +  \
*  ||  RIP  |
*  ||       |
*  ||       |
* \||/\/\//\|/
*/
