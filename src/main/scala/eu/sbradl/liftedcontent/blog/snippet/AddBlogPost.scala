package eu.sbradl.liftedcontent.blog.snippet

import de.sbradl.liftedcontent.core.model.User

import eu.sbradl.liftedcontent.blog.lib.PostHelpers
import eu.sbradl.liftedcontent.blog.model.Post
import eu.sbradl.liftedcontent.blog.model.PostContent

import java.util.Locale

import scala.Array.canBuildFrom

import net.liftweb.http.SHtml.PairStringPromoter.funcPromote
import net.liftweb.http.LiftScreen
import net.liftweb.http.S
import net.liftweb.util.AnyVar.whatVarIs

class AddBlogPost extends LiftScreen {
  
  override def finishButton = <button>{ S ? "SAVE" }</button>
  override def cancelButton = <button>{ S ? "CANCEL" }</button>

  object user extends ScreenVar(User.currentUser.openOr(User.guestUser))
  object userLocale extends ScreenVar(user.locale.isAsLocale)
  
  object post extends ScreenVar(Post.create)
  object content extends ScreenVar(PostContent.create)
  
  val languages = Locale.getAvailableLocales.sortBy(_.getDisplayLanguage(userLocale)).map(_.getLanguage).toList.distinct

  val language = select[String](S ? "LANGUAGE", userLocale.getLanguage, languages)((l: String) => new Locale(l).getDisplayLanguage(userLocale))

  val title = field(S ? "TITLE", "")
  val text = textarea(S ? "TEXT", "")
  val published = field(S ? "PUBLISH_IMMEDIATELY", false)

  def finish {
    content.translator(user.id.is)
    content.language(language.is)
    content.title(title)
    content.text(text)
    content.published(published)
    
    post.author(user.id.is)
    post.contents += content

    post.save

    S.notice(S ? "ENTRY_POSTED")
    S.redirectTo(PostHelpers.linkTo(content))
  }

}