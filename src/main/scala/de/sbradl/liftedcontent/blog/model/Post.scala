package de.sbradl.liftedcontent.blog.model

import de.sbradl.liftedcontent.core.model.User
import net.liftweb.mapper._

object Post extends Post with LongKeyedMetaMapper[Post]

class Post extends LongKeyedMapper[Post] with IdPK with OneToMany[Long, Post] {

  def getSingleton = Post

  object author extends MappedLongForeignKey(this, User)
  
  object comments extends MappedOneToMany(Comment, Comment.post, OrderBy(Comment.createdAt, Ascending)) with Cascade[Comment]
  object contents extends MappedOneToMany(PostContent, PostContent.post) with Cascade[PostContent]

}