import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

val id="51ab2491790c64324987ca511d3b6949caec39582f034d56bd63"
id.substring(0,12)

case class JsType2(n: String)

case class JsType1(v: String, r: String, s: Seq[JsType2])

implicit val apReads: Reads[JsType1] = (
  (JsPath \ "ApiVersion").read[String] and
    (JsPath \ "Arch").read[String] and
    (JsPath \ "arr").read[Seq[JsType2]]
  )(JsType1.apply _)

implicit val arReads: Reads[JsType2] = (
  JsPath).read[String]
  .map(JsType2(_))


val obj = JsString("aa")
val js = JsArray(Seq(obj))

val json1 = Json.obj(
  "ApiVersion" -> "1.14",
  "Arch" -> "amd64",
  "arr" -> js
)
val json2 = Json.obj(
  "ApiVersion" -> "1.15",
  "Arch" -> "amd64",
  "arr" -> js
)
Json.prettyPrint(js)
val json = Json.toJson(Seq(json1, json2))
//Json.prettyPrint(json)
val oo = (json).as[List[JsObject]]
  .map { i =>
  i.validate[JsType1] match {
    case s: JsSuccess[JsType1] => {
      val a: JsType1 = s.get
      Some(a)
    }
    case _  => {
      None
    }
  }
}.flatten

json1.validate[JsType1]
val o = json1.validate[JsType1] match {
  case s: JsSuccess[JsType1] => {
    val a: JsType1 = s.get
    Some(a)
  }
  case _ => {
    None
  }
}


