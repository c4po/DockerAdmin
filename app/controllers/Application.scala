package controllers

import play.api.Logger
import play.api.Play.current
import play.api.libs.json.{JsValue, _}
import play.api.libs.ws._
import play.api.mvc.{Action, Controller}
import utils.Docker.{DockerContainer, DockerImage}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends Controller {

  def index = Action.async {
    version.map(js => Ok(views.html.info(js)))
  }

  def preferences = Action.async {
    version.map(js => Ok(views.html.info(js)))
  }

  def images = Action.async {
    getImages.map(images => Ok(views.html.images(images.flatten)))
  }

  def containers = Action.async {
    getContainers.map(container => Ok(views.html.containers(container.flatten)))
  }

  def getImages: Future[List[Option[DockerImage]]] = {
    WS.url("http://10.102.144.60:4243/images/json").get()
      .map { response =>
      (response.json).as[List[JsObject]]
        .map { i =>
        i.validate[DockerImage] match {
          case s: JsSuccess[DockerImage] => {
            Some(s.get)
          }
          case e: JsError => {
            Logger.error(e.toString)
            None
          }
        }
      }
    }
  }

  def getContainers: Future[List[Option[DockerContainer]]] = {
    WS.url("http://10.102.144.60:4243/containers/json?all=1").get()
      .map { response =>
      (response.json).as[List[JsObject]]
        .map { i =>
        i.validate[DockerContainer] match {
          case s: JsSuccess[DockerContainer] => {
            Some(s.get)
          }
          case e: JsError => {
            //Logger.error("error" + e)
            None
          }
        }
      }
    }
  }

  val version: Future[JsValue] = {
    WS.url("http://10.102.144.60:4243/version").get()
      .map { response => response.json}
  }
}
