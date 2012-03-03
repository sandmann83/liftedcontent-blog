package eu.sbradl.liftedcontent.blog.snippet

import eu.sbradl.liftedcontent.blog.model.Post
import eu.sbradl.liftedcontent.core.model.User
import eu.sbradl.liftedcontent.blog.model.PostContent
import net.liftweb.http.LiftScreen
import net.liftweb.http.S
import net.liftweb.http.SHtml
import java.util.Locale
import net.liftweb.common.Full
import net.liftweb.util.FieldContainer
import net.liftweb.common.Box
import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds
import net.liftweb.http.SessionVar
import net.liftweb.wizard.Wizard
import net.liftweb.http.StatefulSnippet
import net.liftweb.util.Helpers._
import net.liftweb.http.CometListener
import net.liftweb.http.CometActor

class EditPost(post: Post) extends Wizard {
  
  override def finishButton = <button>{ S ? "SAVE" }</button>
  override def cancelButton = <button>{ S ? "CANCEL" }</button>
  override def nextButton = <button>{S ? "NEXT"}</button>
  override def prevButton = <button>{S ? "PREVIOUS"}</button>
  
  private object user extends WizardVar(User.currentUser.openOr(User.guestUser))
  private object userLocale extends WizardVar(user.locale.isAsLocale)
  
  private var content: PostContent = null
  
  val languageScreen = new Screen {
    val languages = Locale.getAvailableLocales.sortBy(_.getDisplayLanguage(userLocale)).map(_.getLanguage).toList.distinct
    
    val language = select[String](S ? "LANGUAGE", userLocale.getLanguage, languages)((l: String) => new Locale(l).getDisplayLanguage(userLocale))
  }
  
  val editScreen = new Screen {

    override def transitionIntoFrom(from: Box[Screen]) {
      val language = languageScreen.language.is
      content = post.contents.find(_.language.isAsLocale.getLanguage == language) match {
        case Some(content) => content
        case _ => PostContent.create.post(post).translator(user.is).language(language)
      }
      
      title.set(content.title)
      text.set(content.text)
      published.set(content.published)
    }
    
    val title = field(S ? "TITLE", "")
    val text = textarea(S ? "TEXT", "")

    val published = field(S ? "PUBLISH", false)
  }
  
  def finish {
    content.title(editScreen.title)
    content.text(editScreen.text)
    content.published(editScreen.published)
    
    post.contents += content
    post.save
  }
}