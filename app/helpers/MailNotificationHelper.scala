package helpers

import akka.actor.Actor
import datalayer.{UserDBHelper, MailNotifDBHelper}
import model.User
import play.api.libs.mailer._
import play.api.Play.current

class MailNotifActor extends Actor {
  override def receive: Receive ={
    case "tick" => MailNotificationHelper.mailNotLoggedUsers()
  }
}

object MailNotificationHelper{

  def testMailer = {
    val testmail = Email(
    "Test email",
    "Vasiyet App <linovivasiyetapp@gmail.com>",
    Seq("Efe Gürkan YALAMAN <efeyalaman@gmail.com>"),
    bodyText = Some("text trial")
    )
    MailerPlugin.send(testmail)
  }

  def publishCheckMail(user:User) {//log in or will publish memorial
    println(user.email)
    val testmail = Email(
      "Test email",
      "Vasiyet App <linovivasiyetapp@gmail.com>",
      Seq("Efe Gürkan YALAMAN <efeyalaman@gmail.com>"),
      bodyText = Some("These user iz ded : " +user.email )
    )
    MailerPlugin.send(testmail)
    MailNotifDBHelper.setIsMailed(user.id)
  }

  def mailNotLoggedUsers(): Boolean = {
    val userIds = MailNotifDBHelper.getNonMailedUsersInteval("MINUTE", 1)
    val users = userIds.map(id=> UserDBHelper.getUserById(id))
    println("Mailing users below")
    users.foreach(u=> publishCheckMail(u))

    false
  }

  def publishMemorial={
    //todo get users with bigger interval, send all contacts a mail.
  }



}
