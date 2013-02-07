package eu.sbradl.liftedcontent.blog.model

import eu.sbradl.liftedcontent.core.model.User
import net.liftweb.mapper._
import net.liftweb.http.S

object PostContent extends PostContent with LongKeyedMetaMapper[PostContent]

class PostContent extends LongKeyedMapper[PostContent] with IdPK with CreatedUpdated
  with OneToMany[Long, PostContent] {

  def getSingleton = PostContent

  object post extends MappedLongForeignKey(this, Post)
  object translator extends MappedLongForeignKey(this, User)
  
  object language extends MappedLocale(this)

  object title extends MappedString(this, 128) {
    override def displayName = S ? "TITLE"
    override def validations = valMinLen(3, S ? "TITLE_TOO_SHOT") _ :: super.validations
  }
  
  object text extends MappedText(this) {
    override def defaultValue = ""
  }
  
  object published extends MappedBoolean(this) {
    override def defaultValue = false
  }

}