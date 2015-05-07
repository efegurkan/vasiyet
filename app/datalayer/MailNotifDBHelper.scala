package datalayer

import org.joda.time.DateTime
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Play.current

object MailNotifDBHelper{

  def checkUserLastLogin(userid: Long): DateTime = {
    DB.withConnection{implicit c =>
      SQL(
        """
          |SELECT loginTime FROM LoginCheck
          |WHERE userid = {userid}
        """.stripMargin).on("userid"->userid).executeQuery().as(scalar[DateTime].single)
    }
  }


  def getNonMailed: List[Long] ={
    DB.withConnection{ implicit  c =>
      SQL(
        """
          |SELECT userid FROM LoginCheck
          |WHERE isMailSent = FALSE
        """.stripMargin).executeQuery().as(scalar[Long] *)

    }
  }

  def getNonMailedUsersInteval(intervalType: String, interval: Int) :List[Long] = {
    DB.withConnection{implicit c =>
      println(interval + " minutes")
      SQL(
        """
          |SELECT userid FROM LoginCheck
          |WHERE isMailSent = FALSE AND loginTime < NOW() - INTERVAL {interval} MINUTE
        """.stripMargin).on("interval"->interval/*, "intervalType"-> intervalType*/).executeQuery().as(scalar[Long] *)
    }
  }

  def setIsMailed(userid : Long): Unit ={
    DB.withConnection{implicit c=>
      SQL(
        """
          |UPDATE LoginCheck
          |SET isMailSent = TRUE
          |WHERE userid = {userid}
        """.stripMargin).on("userid"->userid).executeUpdate()
    }
  }
}
