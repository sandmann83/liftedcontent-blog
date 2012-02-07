package de.sbradl.liftedcontent.blog.snippet

import net.liftweb.util.Helpers._
import de.sbradl.liftedcontent.blog.lib.PostHelpers
import scala.xml.Text
import net.liftweb.textile.TextileParser
import de.sbradl.liftedcontent.util.Highlight
import de.sbradl.liftedcontent.util.Count

class BlogSearchResults(searchTerm: String) {

  val posts = PostHelpers.searchFor(searchTerm)

  def render = {
    "data-lift-id=term *" #> searchTerm &
      "data-lift-id=count *" #> posts.size &
      "data-lift-id=result" #> posts.map {
        post =>
          {
            val matches = (Count(searchTerm) in post.title) + (Count(searchTerm) in post.text)
            "data-lift-id=link *" #> (Highlight(searchTerm) in post.title) &
            "data-lift-id=matches *" #> matches &
            "data-lift-id=link [href]" #> PostHelpers.linkTo(post) &
            "data-lift-id=preview *" #> (Highlight(searchTerm) in PostHelpers.summary(post))
          }
      }
  }

}