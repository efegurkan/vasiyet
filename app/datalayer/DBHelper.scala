package datalayer


import anorm.SqlParser
import anorm._
abstract class DBHelper[T] {

  
  def parser : RowParser[T] 

}
  
  
