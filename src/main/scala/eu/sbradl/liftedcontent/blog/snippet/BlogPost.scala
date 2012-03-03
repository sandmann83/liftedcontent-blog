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
import net.liftweb.textile.TextileParser
import scala.xml.NodeSeq
import eu.sbradl.liftedcontent.util.OnConfirm
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmd
import eu.sbradl.liftedcontent.microformats.snippet.Atom

class BlogPost(post: PostContent) {

  var dateFormat: DateFormat = null

  def render = {
    dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, S.locale)

    "*" #> Atom.entry &
    "* *" #> renderPost(post)
  }

  def editLink(id: Long) = "/blog/edit/" + id.toString

  def delete(post: PostContent): JsCmd = {
	post.delete_!
	
	S.notice(S ? "ENTRY_DELETED")
	
	S.redirectTo("/")
  }

  def renderPost(post: PostContent) = {
    val metaPost = post.post.obj.open_!
    val author = metaPost.author.obj.open_!

    val status = post.published.is match {
      case true => S ? "BLOG_ENTRY_STATUS_PUBLISHED"
      case false => S ? "BLOG_ENTRY_STATUS_UNPUBLISHED"
    }

    "data-lift-id=title *" #> post.title.is &
    Atom.title("data-lift-id=title") &
      "data-lift-id=manage *" #> {
        "data-lift-id=edit [href]" #> editLink(metaPost.id.is) &
          "data-lift-id=delete [onclick]" #> OnConfirm(S ? "REALLY_DELETE_POST", () => delete(post))
      } &
      "data-lift-id=author *" #> author.name &
      Atom.author("data-lift-id=author") &
      "data-lift-id=translator *" #> post.translator.obj.open_!.name &
      "data-lift-id=createdAt *" #> dateFormat.format(post.createdAt.is) &
      Atom.published("data-lift-id=createdAt", dateFormat.format(post.createdAt.is)) &
      "data-lift-id=updatedAt *" #> dateFormat.format(post.updatedAt.is) &
      Atom.updated("data-lift-id=updatedAt", dateFormat.format(post.updatedAt.is)) &
      "data-lift-id=text" #> TextileParser.toHtml(post.text.is) &
      Atom.content &
      renderComments(metaPost)

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