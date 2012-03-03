package eu.sbradl.liftedcontent.blog.snippet

import eu.sbradl.liftedcontent.blog.lib.PostHelpers
import eu.sbradl.liftedcontent.blog.model.PostContent

import java.util.Calendar
import java.util.Date

import scala.collection.SortedMap

import eu.sbradl.liftedcontent.core.lib.ACL
import net.liftweb.http.S
import net.liftweb.mapper.MappedField.mapToType
import net.liftweb.mapper.By
import net.liftweb.mapper.Descending
import net.liftweb.mapper.OrderBy
import net.liftweb.util.Helpers.TimeSpan.dateToTS
import net.liftweb.util.Helpers.TimeSpan
import net.liftweb.util.Helpers.now
import net.liftweb.util.Helpers.strToCssBindPromoter
import net.liftweb.util.IterableConst.itNodeSeqFunc
import net.liftweb.util.TimeHelpers

class BlogArchive {

  def render = {
    val calendar = Calendar.getInstance(S.locale)

    val headerClass = S.attr("li_header").openOr("")
    val monthsToShow = S.attr("months").map(_.toInt).openOr(3)
    val showAll = S.attr("showAll").map(_.toBoolean).openOr(false)
    
    val orderBy = OrderBy(PostContent.createdAt, Descending)
    val published = By(PostContent.published, true)
    val matchLocale = By(PostContent.language, S.locale.getLanguage)

    val posts = ACL.isAllowed("blog/edit") match{
      case true => PostContent.findAll(orderBy)
      case false => PostContent.findAll(published, matchLocale, orderBy)
    } 

    val groupedPosts = SortedMap[Int, List[PostContent]]() ++ posts.groupBy(post => {
      val spanBetween: TimeSpan = (now: TimeSpan) - post.createdAt.is
      calendar.setTime(spanBetween.date)
      calendar.get(Calendar.MONTH)
    })
    
    val postSelection = showAll match {
      case true => groupedPosts
      case false => groupedPosts.take(monthsToShow)
    }

    "* *" #> postSelection.map {
      kv =>
        {
          val post = kv._2.head

          "data-lift-id=header *" #> month(post.createdAt) &
            "data-lift-id=item" #> kv._2.map {
              post =>
                {
                  "* * " #> <a href={ PostHelpers.linkTo(post) }>{ post.title.is }</a>
                }
            }
        }
    }

  }

  private def month(date: Date) = {
    TimeHelpers.month(date)
    val cal = Calendar.getInstance(S.locale)
    cal.setTime(date)

    cal.getDisplayName(Calendar.MONTH, Calendar.LONG, S.locale) +
      " " + cal.get(Calendar.YEAR)
  }

}