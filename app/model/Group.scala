package model

case class Group(pId:Option[Long],
                  pName:String
                  /* TODO add members*/) {
  //TODO create Group model

  private val _id = pId
  private val _name = pName


  //Getters
  def id = _id
  def name = _name
}
