package eu.sbradl.liftedcontent.blog.snippet

import eu.sbradl.liftedcontent.blog.lib.PostHelpers
import eu.sbradl.liftedcontent.blog.model.Comment
import eu.sbradl.liftedcontent.blog.model.PostContent
import eu.sbradl.liftedcontent.core.model.User
import net.liftweb.http.js.JsCmd.unitToJsCmd
import net.liftweb.http.js.JsCmd
import net.liftweb.http.S
import net.liftweb.http.SHtml
import net.liftweb.mapper.MappedForeignKey.getObj
import net.liftweb.util.Helpers.nextFuncName
import net.liftweb.util.Helpers.strToCssBindPromoter
import net.liftweb.util.StringPromotable.jsCmdToStrPromo
import net.liftweb.common.Full

class CommentBlogPost(postContent: PostContent) {
  val postBox = postContent.post.obj

  def render = {
    val name = nextFuncName
    var comment = ""

    "name=comment" #> SHtml.ajaxTextarea("", comment = _) &
      "type=submit [onclick]" #> SHtml.onEvent((s) => saveComment(comment))
  }

  def saveComment(text: String): JsCmd = postBox match {
    case Full(post) => {
      val comment = Comment.create
      comment.post(post)
      comment.author(User.currentUser.openOr(User.guestUser))
      comment.comment(text)

      comment.validate match {
        case List() => {
          comment.save
          S.notice(S ? "COMMENT_SAVED")
          S.redirectTo(PostHelpers.linkTo(postContent))
        }
        case errors => errors foreach {
          error => S.notice(error.msg)
        }
      }
    }
    case _ => {
    	S.warning(S ? "POSTCONTENT_NOT_FOUND")
    }

  }

}