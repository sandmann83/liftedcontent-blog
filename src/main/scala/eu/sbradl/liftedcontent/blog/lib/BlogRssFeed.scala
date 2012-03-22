package eu.sbradl.liftedcontent.blog.lib

import net.liftweb.http._
import net.liftweb.http.rest._
import eu.sbradl.liftedcontent.blog.model.PostContent
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Descending
import net.liftweb.mapper.By
import eu.sbradl.liftedcontent.util.RssFeed
import eu.sbradl.liftedcontent.core.model.User

object BlogRssFeed extends RestHelper {
  serve {
    case XmlGet("blog" :: "rss" :: _, _) => {
      val posts = PostContent.findAll(
        By(PostContent.published, true),
        By(PostContent.language, S.locale.getLanguage),
        OrderBy(PostContent.createdAt, Descending))

      RssFeed[PostContent](S.hostName + " Blog", S.hostAndPath, 
          S.hostName + " Blog", S.locale.toString, posts.head.translator.open_!.name, 
          posts, _.title, PostHelpers.summary(_, 2, 200).text,
          S.hostAndPath + PostHelpers.linkTo(_), 
          p => "%s, %s".format(p.translator.open_!.name, p.translator.open_!.email), 
          p => LiftRules.dateTimeConverter.vend.formatDateTime(p.createdAt))
    }
  }
}