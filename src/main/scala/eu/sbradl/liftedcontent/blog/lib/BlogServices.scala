package eu.sbradl.liftedcontent.blog.lib

import net.liftweb.http._
import net.liftweb.http.rest._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.common.Full
import eu.sbradl.liftedcontent.blog.model.PostContent

object BlogServices extends RestHelper {

  serve {
    case "blog" :: "save" :: _ JsonPut json -> _ => save(json)
  }
  
  private def save(input: JValue) = {
    println(compact(render(input)))
    
    val regions = input \ "content" children

    val head = extractData(regions.head)

    val id = head._1
    val title = head._2

    PostContent.find(id) match {
      case Full(post) => {
        post.title(title)
        post.text(extractData(regions.tail.head)._2)
        post.save

        new OkResponse
      }
      case _ => PlainTextResponse("invalid page id", 500)
    }
  }

  private def extractData(data: JValue) = {
    val field = data.asInstanceOf[JField]

    (field.name, (field \ "value").asInstanceOf[JString].values)
  }
  
}