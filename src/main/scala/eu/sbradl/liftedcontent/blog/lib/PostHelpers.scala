package eu.sbradl.liftedcontent.blog.lib

import eu.sbradl.liftedcontent.blog.model.PostContent

import scala.xml.Text

import net.liftweb.common.Box.option2Box
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.mapper.MappedField.mapToType
import net.liftweb.mapper.By
import net.liftweb.mapper.Cmp
import net.liftweb.mapper.Descending
import net.liftweb.mapper.OprEnum
import net.liftweb.mapper.OrderBy
import net.liftweb.textile.TextileParser
import net.liftweb.util.Helpers.urlEncode

object PostHelpers {

  def latest = PostContent.find(
      By(PostContent.published, true), 
      By(PostContent.language, S.locale.getLanguage),
      OrderBy(PostContent.createdAt, Descending)) match {
    case box@Full(post) => box
    case _ => PostContent.find(
      By(PostContent.published, true),
      OrderBy(PostContent.createdAt, Descending))
  }
  
  def plainText(post: PostContent) = TextileParser.toHtml(post.title).text + ": " + TextileParser.toHtml(post.text).text
  def plainTextWithoutTitle(post: PostContent) = TextileParser.toHtml(post.text).text

  def linkTo(post: PostContent) = "/blog/" + urlEncode(post.title)

  def searchFor(term: String) = {
    val results = searchForTitle(term) ++ searchForText(term)
    val posts = results.distinct
    
    posts.sortBy(post => post.title.split(term).size + post.text.split(term).size) reverse
  }

  private def searchForTitle(term: String) = PostContent.findAll(
    By(PostContent.published, true),
    Cmp(PostContent.title, OprEnum.Like, Full("%" + term.toLowerCase + "%"), None, Full("lower")))

  private def searchForText(term: String) = PostContent.findAll(
    By(PostContent.published, true),
    Cmp(PostContent.text, OprEnum.Like, Full("%" + term.toLowerCase + "%"), None, Full("lower")))

  def summary(post: PostContent, desiredSentences: Int = 3, maxCharacters: Int = 100) = {
    val fullText = post.text.is

    val punctuationMarks = List('.', '!', '?')

    val numSentences = fullText.count(c => punctuationMarks.contains(c))

    var summary = fullText

    if (numSentences >= desiredSentences) {
      var currentSentence = 1
      summary = fullText.takeWhile(c => {
        if (punctuationMarks.contains(c)) {
          currentSentence += 1

          true
        } else {
          currentSentence <= desiredSentences
        }
      })
    } else {
      summary = fullText.take(maxCharacters)
    }
    
    if(summary.endsWith(".")) {
      summary = summary + ".."
    } else {
      summary = summary + "..."
    }

    Text(TextileParser.toHtml(summary).text)
  }

}