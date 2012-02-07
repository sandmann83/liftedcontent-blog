package de.sbradl.liftedcontent.blog.snippet

import de.sbradl.liftedcontent.blog.model.PostContent
import net.liftweb.http.LiftScreen
import net.liftweb.http.S

class DeleteBlogPostContent(content: PostContent) extends LiftScreen {

  override def finishButton = <button>{ S ? "YES" }</button>
  override def cancelButton = <button>{ S ? "NO" }</button>

  def finish {
    content.delete_!

    S.notice(S ? "ENTRY_DELETED")
    S.redirectTo("/blog/")
  }

}