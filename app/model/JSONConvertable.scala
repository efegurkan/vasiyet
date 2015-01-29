package model

import play.api.libs.json.JsValue

trait JSONConvertable[T] {
  def toJSON(t:T): JsValue
  def fromJSON(json : JsValue): T
}
