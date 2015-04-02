package helpers

import model.{User, Post}

object PostLocker {

  def lockPosts(merhum:User) = {
    val posts= getPosts(merhum)
    posts.foreach(p=>Post.lockPost(p))
  }

  def getPosts(merhum:User):List[Post] = {
    Post.getPosts(merhum.id)
  }

}
