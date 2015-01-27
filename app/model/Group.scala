package model

case class EditGroupForm(id:Option[Long],
                          name:String,
                          members: List[Contact] = List.empty
                  )

case class Group(id:Option[Long],
                  name:String,
                  members: List[Contact] = List.empty
                  )
