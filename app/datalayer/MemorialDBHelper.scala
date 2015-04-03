package datalayer

import model.{Contact, User}
import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._

object MemorialDBHelper {
  def getMemorialId(user: User): Long = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |SELECT id FROM vasiyet.Memorial
          |WHERE owner = {userid}
        """.stripMargin)
        .on("userid" -> user.id)

      query.executeQuery().as(scalar[Long].single)

    }
  }


  def createMemorial(user: User): Long = {
    DB.withConnection { implicit c =>
      val query = SQL(
        """
          |INSERT INTO vasiyet.Memorial (id, owner, ownerName, ownerSurname)
          |VALUES (Null, {userid}, {username}, {usersurname})
        """.stripMargin)
        .on("userid" -> user.id, "username" -> user.name, "usersurname" -> user.surname)

      val inserted: Option[Long] = query.executeInsert()
      inserted.get
    }
  }

  def generateLookupTable(memorialId: Long, contacts: List[Contact]) = {
    DB.withConnection { implicit connection =>

      val indexedValues = contacts.zipWithIndex
      val rows = indexedValues.map { case (value, i) =>
        s"(Null, {val2_${i}}, {val3_${i}})"
      }.mkString(",")

      val parameters = indexedValues.flatMap { case (contact, i) =>
        Seq(
          NamedParameter(s"val2_${i}", memorialId),
          NamedParameter(s"val3_${i}", contact.email)
        )
      }

      println(parameters)
      SQL(
        """
          |INSERT INTO vasiyet.MemorialLookup(id, memorialId, contactemail) VALUES
        """.stripMargin + rows).on(parameters: _ *).executeUpdate()
    }
  }
}
