package eu.sbradl.liftedcontent.blog.model

import de.sbradl.liftedcontent.core.model.User
import net.liftweb.http.S
import net.liftweb.mapper.CreatedUpdated
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedLongForeignKey
import net.liftweb.mapper.MappedTextarea

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