package de.sbradl.liftedcontent.blog

import de.sbradl.liftedcontent.blog.model.Comment
import de.sbradl.liftedcontent.blog.model.Post
import de.sbradl.liftedcontent.blog.model.PostContent
import net.liftweb.http.S
import net.liftweb.sitemap.Loc.LinkText.strToLinkText
import net.liftweb.sitemap.LocPath.stringToLocPath
import net.liftweb.sitemap.Loc.Hidden
import net.liftweb.sitemap.Loc.LocGroup
import net.liftweb.sitemap.Menu
import de.sbradl.liftedcontent.util.Module
import de.sbradl.liftedcontent.core.lib.ACL
import net.liftweb.sitemap.Loc.LinkText
import scala.xml.Text
import net.liftweb.mapper.By
import net.liftweb.util.Helpers._
import net.liftweb.http.LiftRules
import net.liftweb.util.NamedPF
import net.liftweb.http.RewriteRequest
import net.liftweb.http.ParsePath
import net.liftweb.http.RewriteResponse
import lib.PostHelpers
import net.liftweb.widgets.autocomplete.AutoComplete
import net.liftweb.common.Full

class BlogModule extends Module {

  def name = "Blog"

  override def mappers = List(Post, PostContent, Comment)

  override def menus = List(
    Menu.param[PostContent]("BLOG", 
        new LinkText(p => Text(p.title)), 
        url => PostContent.find(By(PostContent.title, urlDecode(url))), 
        p => urlEncode(p.title)) / "blog",
        
    Menu.i("BLOG_ARCHIVE") / "blog" / "archive" >> LocGroup("primary") >> ACL.locParam("blog/archive"),
    
    Menu.param[String]("BLOG_SEARCH", 
        new LinkText(s => Text((S ? "SEARCH") + ": " + s)), 
        s => Full(urlDecode(s)), 
        s => urlEncode(s)) / "blog" / "search",
        
    Menu.i("BLOG_OVERVIEW") / "blog" / "index" >> LocGroup("primary") >> ACL.locParam("blog/index") submenus (
      Menu.i("POST_BLOG_ENTRY") / "blog" / "post" >> LocGroup("primary") >> ACL.locParam("blog/post"),
      Menu.param[Post]("CommentBlogEntry", S ? "WRITE_COMMENT", Post.find, _.id.is.toString) / "blog" / "comment" >> Hidden >> ACL.locParam("blog/comment"),
      Menu.param[Post]("EditBlogEntry", S ? "EDIT_BLOG_ENTRY", Post.find, _.id.is.toString) / "blog" / "edit" >> Hidden >> ACL.locParam("blog/edit"),
      Menu.param[Post]("DeleteBlogEntry", S ? "DELETE_BLOG_ENTRY", Post.find, _.id.is.toString) / "blog" / "delete" >> Hidden >> ACL.locParam("blog/delete")))

  override def init {
    super.init

    LiftRules.statefulRewrite.prepend(NamedPF("LatestBlogPostRewrite") {
      case RewriteRequest(
        ParsePath("blog" :: "latest" :: Nil, _, _, _), _, _) => {
          
        RewriteResponse("blog" :: urlEncode(PostHelpers.latest.map(_.title.is).openOr("")) :: Nil)
      }
    })
  }
}