package utility

import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent._

object AuthAction extends ActionBuilder[Request] {

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]) = {
    if (request.session.isEmpty) {
      Future.successful(Redirect("/login"))
    } else {
      block(request)
    }
  }
}
