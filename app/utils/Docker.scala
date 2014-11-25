package utils

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.Logger

/**
 * Created by max.cai on 14-11-24.
 */
object Docker {
  val info = List("Version", "ApiVersion", "Os", "Arch", "KernelVersion")

  //(json ).as[List[JsObject]]

  case class RepoTags(repo: String, tags: String)

  case class DockerImage(created: Int, id: String, parentId: String, repoTags: Seq[RepoTags], size: Int, virtualSize: Int){
    def idInShort:String = id.toUpperCase.substring(0,12)
  }

  implicit val repoTagsReads: Reads[RepoTags] = {
    val pattern = """(.*):(.*)""".r
    JsPath.read[String]
      .map { st => val pattern(repo, tags) = st; RepoTags(repo, tags)}
  }

  implicit val imagesReads: Reads[DockerImage] = {
    (
      (JsPath \ "Created").read[Int] and
        (JsPath \ "Id").read[String] and
        (JsPath \ "ParentId").read[String] and
        (JsPath \ "RepoTags").read[Seq[RepoTags]] and
        (JsPath \ "Size").read[Int] and
        (JsPath \ "VirtualSize").read[Int]
      )(DockerImage.apply _ )
  }


  case class ContainerName(name:String)
  case class ContainerPort(ip:Option[String], privatePort:Option[Int], publicPort:Option[Int], ipType:Option[String])
  case class DockerContainer(command:String, created:Int, id:String, image:String,  status:String){
    def idInShort:String = id.toUpperCase.substring(0,12)
  }

  implicit val containersReads: Reads[DockerContainer] = (
      (JsPath \ "Command").read[String] and
        (JsPath \ "Created").read[Int] and
        (JsPath \ "Id").read[String] and
        (JsPath \ "Image").read[String] and
        // (JsPath \ "Names").read[Seq[ContainerName]] and
        //(JsPath \ "Ports").read[ ]and
        (JsPath \ "Status").read[String]
      )(DockerContainer.apply _)

  implicit val containerNameReads: Reads[ContainerName] = {
    JsPath.read[String].map(ContainerName.apply _)
  }
  implicit val containerPortReads: Reads[ContainerPort] = {
    //JsPath.as[List[JsObject]]
    (
      (JsPath \"IP").readNullable[String] and
        (JsPath \"PrivatePort").readNullable[Int] and
        (JsPath \"PublicPort").readNullable[Int] and
        (JsPath \"Type").readNullable[String]
      )(ContainerPort.apply _)
  }

//  name:Seq[ContainerName], ports:Seq[ContainerPort],

}
