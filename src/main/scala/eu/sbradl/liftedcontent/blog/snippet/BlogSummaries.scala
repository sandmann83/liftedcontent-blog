package eu.sbradl.liftedcontent.blog.snippet

import eu.sbradl.liftedcontent.blog.lib.PostHelpers
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.util.Helpers._

class BlogSummaries {

  def render = {
    val n = S.attr("n").map(_.toInt).openOr(3)

    "data-lift-id=post" #> PostHelpers.latest(n).map {
      post =>
        {
          "data-lift-id=title *" #> post.title.is &
            "data-lift-id=summary *" #> PostHelpers.summary(post) &
            "data-lift-id=link [href]" #> PostHelpers.linkTo(post)
        }
    }

  }

}