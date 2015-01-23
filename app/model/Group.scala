package model


case class Group(id:Option[Long],
                  name:String,
                  members: List[Contact] = List.empty
                  )
