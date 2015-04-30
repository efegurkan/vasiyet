package datalayer

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB

object PostPagination {

  //returns triple tuple (start:Long, end:Long, activePage:Long,max:Long)
  def calculatePagination(userId: Long, pageNum: Long): (Long, Long, Long, Long) = {
    DB.withConnection { implicit c =>
      val pageElementCount = 10
      val convertedPageNum = pageNum - 1

      val pageQuery = SQL(
        """
          |SELECT COUNT(*)
          |FROM Post
          |WHERE sender = {id}
        """.
          stripMargin).on("id" -> userId)

      val totalElementCount = pageQuery.executeQuery().as(scalar[Long].single)

      val maxPageNumber = math.ceil(totalElementCount.toDouble / pageElementCount.toDouble)
      println(maxPageNumber)

      val start = if (maxPageNumber == 0) {
        0
      } else if (maxPageNumber < pageNum) {
        pageElementCount * (maxPageNumber - 1)
      } else {
        pageElementCount * convertedPageNum
      }
      println(start)

      val end = if (maxPageNumber < pageNum) {
        maxPageNumber * pageElementCount
      } else {
        start + pageElementCount
      }
      println(end)
      val activepage = (end / pageElementCount).toLong
      (start.toLong, end.toLong, activepage.toLong, maxPageNumber.toLong)
    }
  }
}
