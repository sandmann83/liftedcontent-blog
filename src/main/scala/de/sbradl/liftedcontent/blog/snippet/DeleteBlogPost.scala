package de.sbradl.liftedcontent.blog.snippet

import de.sbradl.liftedcontent.blog.model.Post
import net.liftweb.http.LiftScreen
import net.liftweb.http.S

class DeleteBlogPost(post: Post) extends LiftScreen {

  override def finishButton = <button>{ S ? "YES" }</button>
  override def cancelButton = <button>{ S ? "NO" }</button>

  def finish {
    post.delete_!

    S.notice(S ? "ENTRY_DELETED")
    S.redirectTo("/blog/")
  }

}