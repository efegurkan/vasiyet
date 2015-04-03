package helpers

import play.api.libs.mailer._
import play.api.Play.current

object MailNotificationHelper {

  def testMailer = {
    val testmail = Email(
    "Test email",
    "Vasiyet App <linovivasiyetapp@gmail.com>",
    Seq("Efe Gürkan YALAMAN <efeyalaman@gmail.com>"),
    bodyText = Some("text trial")
    )
    MailerPlugin.send(testmail)
  }
}
