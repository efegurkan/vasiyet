import akka.actor.Props
import akka.actor.Props.apply
import play.api._
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import helpers.MailNotifActor
import scala.concurrent.duration.DurationInt

object Global extends GlobalSettings {
  override def onStart(app: Application) {

    play.api.Play.mode(app) match {
      case _ => mailNotifier(app)
    }
  }

  def mailNotifier(app: Application) = {
    Logger.info("Mail notification scheduling")
    val notifier = Akka.system(app).actorOf(Props(new MailNotifActor()))

    Akka.system(app).scheduler.schedule(0 seconds,10 seconds, notifier, "tick")
  }
}
