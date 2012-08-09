package eu.sbradl.liftedcontent.blog.snippet

import eu.sbradl.liftedcontent.blog.model.PostContent
import eu.sbradl.liftedcontent.blog.lib.PostHelpers
import net.liftweb.util.Helpers._

class BlogMetaTags(post: PostContent) {

  def render = {
    "*" #> {
      <meta name="description" content={PostHelpers.summary(post, Integer.MAX_VALUE, 160)} />
      <meta name="author" content={post.translator.obj.map(_.name).openOr("Unknown")} />
    }
  }

}