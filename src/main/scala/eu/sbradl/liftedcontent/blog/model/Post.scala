package eu.sbradl.liftedcontent.blog.model

import eu.sbradl.liftedcontent.core.model.User
import net.liftweb.mapper.OneToMany
import net.liftweb.mapper.Ascending
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.MappedLongForeignKey

object Post extends Post with LongKeyedMetaMapper[Post]

class Post extends LongKeyedMapper[Post] with IdPK with OneToMany[Long, Post] {

  def getSingleton = Post

  object author extends MappedLongForeignKey(this, User)
  
  object comments extends MappedOneToMany(Comment, Comment.post, OrderBy(Comment.createdAt, Ascending)) with Cascade[Comment]
  object contents extends MappedOneToMany(PostContent, PostContent.post) with Cascade[PostContent]

}