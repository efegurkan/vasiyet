package model

import org.joda.time.DateTime

case class Post(pId : Option[Long], 
				pTitle : String, 
				pContent : String,  
				pFilepath : Option[String],
				pSender: Long,
				pDate : DateTime){
  private val _id = pId
  private val _title = pTitle
  private val _content = pContent
  private val _sender = pSender
  private val _filepath = pFilepath
  private val _dateTime = pDate
  
  //Getters
  def id = _id
  def title = _title
  def content = _content
  def sender = _sender
  def filepath = _filepath
  def dateTime = _dateTime
  

}