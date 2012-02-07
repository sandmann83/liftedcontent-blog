package de.sbradl.liftedcontent.blog.snippet

import de.sbradl.liftedcontent.blog.model.Comment
import de.sbradl.liftedcontent.blog.model.PostContent
import de.sbradl.liftedcontent.core.model.User
import net.liftweb.http.js.JsCmd.unitToJsCmd
import net.liftweb.http.js.JsCmd
import net.liftweb.http.S
import net.liftweb.http.SHtml
import net.liftweb.mapper.MappedForeignKey.getObj
import net.liftweb.util.Helpers.nextFuncName
import net.liftweb.util.Helpers.strToCssBindPromoter
import net.liftweb.util.StringPromotable.jsCmdToStrPromo
import de.sbradl.liftedcontent.blog.lib.PostHelpers

class CommentBlogPost(postContent: PostContent) {
  val post = postContent.post.open_!

  def render = {
    val name = nextFuncName
    var comment = ""
      
    "name=comment" #> SHtml.ajaxTextarea("", comment = _) &
    "type=submit [onclick]" #> SHtml.onEvent((s) => saveComment(comment))
  }
  
  def saveComment(text: String): JsCmd = {
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

}