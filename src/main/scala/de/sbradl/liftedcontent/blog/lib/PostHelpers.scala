package de.sbradl.liftedcontent.blog.lib

import de.sbradl.liftedcontent.blog.model.PostContent
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Descending
import net.liftweb.util.Helpers._
import net.liftweb.util.StringHelpers
import net.liftweb.mapper.By
import net.liftweb.mapper.Like
import net.liftweb.textile.TextileParser
import scala.xml.Text
import java.util.regex.Matcher
import net.liftweb.http.S
import net.liftweb.common.Full

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

  def linkTo(post: PostContent) = "/blog/" + urlEncode(post.title)

  def searchFor(term: String) = {
    val results = searchForTitle(term) ++ searchForText(term)
    val posts = results.distinct
    
    posts.sortBy(post => post.title.split(term).size + post.text.split(term).size) reverse
  }

  private def searchForTitle(term: String) = PostContent.findAll(
    By(PostContent.published, true),
    Like(PostContent.title, "%" + term + "%"))

  private def searchForText(term: String) = PostContent.findAll(
    By(PostContent.published, true),
    Like(PostContent.text, "%" + term + "%"))

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

    Text(TextileParser.toHtml(summary).text)
  }

}