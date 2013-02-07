package eu.sbradl.liftedcontent.blog.snippet

import eu.sbradl.liftedcontent.blog.model.Post
import eu.sbradl.liftedcontent.blog.model.PostContent
import net.liftweb.mapper._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import eu.sbradl.liftedcontent.core.model.User
import eu.sbradl.liftedcontent.core.lib.ACL
import scala.xml.Text
import java.text.DateFormat
import eu.sbradl.liftedcontent.blog.model.Comment
import java.util.Date
import scala.xml.NodeSeq
import eu.sbradl.liftedcontent.util.OnConfirm
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmd
import eu.sbradl.liftedcontent.microformats.snippet.Atom
import net.liftmodules.textile.TextileParser
import net.liftweb.util.ClearNodes
import net.liftweb.common.Full
import scala.xml.Unparsed
import net.liftweb.util.PassThru

class BlogPost(post: PostContent) {

  var dateFormat: DateFormat = null

  def render = {
    dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, S.locale)

    "* *" #> renderPost(post)
  }

  def publish(post: PostContent): JsCmd = {
    post.published(true)
    post.save

    S.notice(S ? "POST_PUBLISHED")
  }

  def delete(post: PostContent): JsCmd = {
    post.delete_!

    S.notice(S ? "ENTRY_DELETED")

    S.redirectTo("/")
  }

  def renderPost(post: PostContent) = {
    if (post.published.is == false && !User.currentUser.map(_.superUser.is).openOr(false)) {
      "*" #> ClearNodes
    } else {
      val metaPost = post.post.obj
      val authorBox = metaPost.map(_.author.obj)

      val status = post.published.is match {
        case true => S ? "BLOG_ENTRY_STATUS_PUBLISHED"
        case false => S ? "BLOG_ENTRY_STATUS_UNPUBLISHED"
      }

      authorBox match {
        case Full(Full(author)) => {
          "data-lift-id=title [id]" #> post.id.is &
            "data-lift-id=title *" #> post.title.is &
            "data-lift-id=author *" #> author.name &
            "data-lift-id=translator *" #> post.translator.obj.map(_.name).openOr("Unknown") &
            "data-lift-id=createdAt *" #> dateFormat.format(post.createdAt.is) &
            "data-lift-id=updatedAt *" #> dateFormat.format(post.updatedAt.is) &
            "data-lift-id=text" #> Unparsed(post.text.is) &
            "data-lift-id=manage *" #> {
              "data-lift-id=translate [href]" #> metaPost.map(p => "/blog/translate/%s".format(p.id.is)) &
              "data-lift-id=publish [onclick]" #> SHtml.onEvent(s => publish(post)) &
                "data-lift-id=delete [onclick]" #> OnConfirm(S ? "REALLY_DELETE_POST", () => delete(post))
            } & (metaPost match {
              case Full(mp) => renderComments(mp)
              case _ => "*" #> PassThru
            })
        }
        case _ => ClearNodes
      }
    }
  }

  def renderComments(metaPost: Post) = metaPost.comments.isEmpty match {
    case true => {
      "data-lift-id=summary" #> (S ? ("COMMENT_SUMMARY", metaPost.comments.size)) &
        "data-lift-id=comment" #> NodeSeq.Empty
    }
    case false => {
      "data-lift-id=comments *" #> {
        "data-lift-id=summary" #> (S ? ("COMMENT_SUMMARY", metaPost.comments.size)) &
          "data-lift-id=commentUrl [href]" #> ("/blog/comment/" + metaPost.id.is) &
          "data-lift-id=commentLinkText" #> (S ? "WRITE_COMMENT") &
          "data-lift-id=comment *" #> metaPost.comments.map {
            comment =>
              {
                "data-lift-id=author" #> comment.author.obj.map(_.name).openOr("unknown") &
                  "data-lift-id=createdAt" #> dateFormat.format(comment.createdAt.is) &
                  "data-lift-id=text *" #> comment.comment.is
              }
          }
      }
    }
  }

}