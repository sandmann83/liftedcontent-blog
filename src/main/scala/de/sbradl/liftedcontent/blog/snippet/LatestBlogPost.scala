package de.sbradl.liftedcontent.blog.snippet

import net.liftweb.util.Helpers._
import de.sbradl.liftedcontent.blog.lib.PostHelpers
import net.liftweb.common.Full
import scala.xml.NodeSeq

class LatestBlogPost {

  // TODO: only render if post is available
  def render = {
    PostHelpers.latest match {
      case Full(post) => {
        "data-lift-id=title *" #> post.title.is &
          "data-lift-id=summary *" #> PostHelpers.summary(post) &
          "data-lift-id=link [href]" #> PostHelpers.linkTo(post)
      }
      case _ => {
        "data-lift-id=title *" #> "Title" &
          "data-lift-id=summary *" #> "Summary"
      }
    }

  }

}