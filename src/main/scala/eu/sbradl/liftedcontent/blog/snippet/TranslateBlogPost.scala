package eu.sbradl.liftedcontent.blog.snippet

import eu.sbradl.liftedcontent.core.lib.BaseScreen
import net.liftweb.http.S
import eu.sbradl.liftedcontent.core.model.User
import eu.sbradl.liftedcontent.blog.model.Post
import eu.sbradl.liftedcontent.blog.model.PostContent
import java.util.Locale
import eu.sbradl.liftedcontent.blog.lib.PostHelpers

class TranslateBlogPost(post: Post) extends BaseScreen {

  def formName = "wizardAll"
    
  override def screenTitle = <h2>{S ? "TRANSLATE_BLOG_POST"}</h2>

  object user extends ScreenVar(User.currentUser.openOr(User.guestUser))
  object userLocale extends ScreenVar(user.locale.isAsLocale)
  
  object content extends ScreenVar(PostContent.create)
  
  val languages = Locale.getAvailableLocales.sortBy(_.getDisplayLanguage(userLocale)).map(_.getLanguage).toList.distinct

  val language = select[String](S ? "LANGUAGE", userLocale.getLanguage, languages)((l: String) => new Locale(l).getDisplayLanguage(userLocale))

  addFields(() => content.title)

  def finish {
    content.translator(user.id.is)
    content.language(language.is)
    content.save
    
    post.author(user.id.is)
    post.contents += content
    post.save

    S.notice(S ? "ENTRY_POSTED")
    S.redirectTo(PostHelpers.linkTo(content))
  }
  
}