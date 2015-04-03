package helpers

import model.{User, Post}
import play.api.Logger

object PostLocker {

  def lockPosts(merhum:User) = {
    Logger.info("lock posts started")
    val posts= getPosts(merhum)
    Logger.info("get Posts worked correctly")
    print("posts:"+ posts)
    posts.foreach(p=>Post.lockPost(p))
  }

  def getPosts(merhum:User):List[Post] = {
    Post.getPosts(merhum.id)
  }

}
