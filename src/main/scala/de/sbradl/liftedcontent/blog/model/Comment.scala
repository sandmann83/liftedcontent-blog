package de.sbradl.liftedcontent.blog.model

import de.sbradl.liftedcontent.core.model.User
import net.liftweb.mapper._
import net.liftweb.http.S

object Comment extends Comment with LongKeyedMetaMapper[Comment] {
  
}

class Comment extends LongKeyedMapper[Comment] with IdPK with CreatedUpdated {

  def getSingleton = Comment
  
  object post extends MappedLongForeignKey(this, Post)
  object author extends MappedLongForeignKey(this, User)
  
  object comment extends MappedTextarea(this, 140) {
    override def validations = valMinLen(2, S ? "COMMENT_TOO_SHORT") _ :: super.validations
    override def setFilter = notNull _ :: trim _ :: super.setFilter
  }
  
}