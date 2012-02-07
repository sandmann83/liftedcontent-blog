package de.sbradl.liftedcontent.blog.model

import de.sbradl.liftedcontent.core.model.User
import net.liftweb.mapper._

object PostContent extends PostContent with LongKeyedMetaMapper[PostContent]

class PostContent extends LongKeyedMapper[PostContent] with IdPK with CreatedUpdated
  with OneToMany[Long, PostContent] {

  def getSingleton = PostContent

  object post extends MappedLongForeignKey(this, Post)
  object translator extends MappedLongForeignKey(this, User)
  
  object language extends MappedLocale(this)

  object title extends MappedString(this, 128)
  object text extends MappedText(this)
  
  object published extends MappedBoolean(this)

}