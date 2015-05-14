package helpers

import akka.actor.Actor
import datalayer.{UserDBHelper, MailNotifDBHelper}
import model.{Memorial, User}
import play.api.libs.mailer._
import play.api.Play.current

class MailNotifActor extends Actor {
  override def receive: Receive ={
    case "inactivity" => MailNotificationHelper.mailInactiveUsers()
    case "memorial"=> MailNotificationHelper.publishMemorial()
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

  def notifyMemorialMail(memorialid:Long, contactMail: String): Unit = {
    println("memorial mail")
    val memorialNotificationMail = Email(
      "Test email",
      "Vasiyet App <linovivasiyetapp@gmail.com>",
      Seq("Efe Gürkan YALAMAN <efeyalaman@gmail.com>"),
      bodyText = Some("http://localhost:9000/memorial/"+memorialid)
    )

    MailerPlugin.send(memorialNotificationMail)
  }

  def mailInactiveUsers(): Boolean = {
    val userIds = MailNotifDBHelper.getNonMailedUsersInteval("MINUTE", 1)
    val users = userIds.map(id=> UserDBHelper.getUserById(id))
    println("Mailing users below")
    users.foreach(u=> publishCheckMail(u))

    false
  }

  def publishMemorial()={
    //todo get users with bigger interval, send all contacts a mail.
    //todo get mailed users from db on bigger interval
    val userIds = MailNotifDBHelper.getMailedUsersInterval("MINUTE",5)
    val users = userIds.map(id=>UserDBHelper.getUserById(id))

    println("Publishing these users memorial")
    println(users)

    users.foreach(user =>{
    val memoId = PublishHelper.publishMemorial(user.id)

    val contacts = MemorialHelper.getMemorialContactEmails(memoId)

    contacts.foreach(e=> notifyMemorialMail(memoId,e))
    }
    )
  }



}
