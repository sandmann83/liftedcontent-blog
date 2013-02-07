package eu.sbradl.liftedcontent.blog.snippet

import eu.sbradl.autocomplete.AutoComplete
import eu.sbradl.liftedcontent.blog.lib.PostHelpers
import eu.sbradl.liftedcontent.blog.model.PostContent

import scala.xml.Text

import net.liftweb.common.Full
import net.liftweb.http.js.JE.ValById
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds
import net.liftweb.http.S
import net.liftweb.http.SHtml
import net.liftweb.mapper.By
import net.liftweb.util.Helpers.strToCssBindPromoter
import net.liftweb.util.Helpers.urlEncode
import net.liftweb.util.StringPromotable.jsCmdToStrPromo

class SearchBlogPost {
  
  def process(name: String): JsCmd = name.isEmpty match {
    case true => JsCmds.Noop
    case false => {
      PostContent.find(By(PostContent.title, name)) match {
        case Full(post) => S.redirectTo(PostHelpers.linkTo(post))
        case _ => S.redirectTo("/blog/search/" + urlEncode(name))
      }
    }
  }

  def render = {
    val autoComplete = AutoComplete((current: String) => {
      val posts = PostHelpers.searchFor(current)
      
      posts map {post: PostContent => (post.title.is, post.text.is) }
    })

    val name = autoComplete.inputName
    
    val go = SHtml.ajaxCall(ValById(name), (s) => process(s.trim))

    "data-lift-id=datalist" #> autoComplete.datalist &
      "name=search [name]" #> name &
      "name=search [id]" #> name &
      "name=search [oninput]" #> autoComplete.onChange &
      "name=search [list]" #> autoComplete.datalistId &
      "name=search [placeholder]" #> Text(S ? "SEARCH") &
      "type=submit [onclick]" #> go
  }

}