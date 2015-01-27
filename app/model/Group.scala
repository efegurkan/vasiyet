package model

case class EditGroupForm(id:Option[Long],
                          name:String
                  )

case class Group(id:Option[Long],
                  name:String,
                  members: List[Contact] = List.empty
                  )

case class AddMemberData(contactId: Option[Long], groupId: Option[Long])

object Group {
  def editGroup(form: EditGroupForm) : Boolean = {
    //todo not implemented
    //todo determine add or edit
    false
  }

  def deleteGroup(form: Long): Boolean = {
    //todo not implemented
    false
  }

  def addMember(data: AddMemberData) : Boolean = {
    //todo not implemented
    false
  }

  def deleteMember(data: AddMemberData) : Boolean = {
    //todo not implemented
    false
  }
}
