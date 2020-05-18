package tftcomps.domain

import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveCodec
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Codec, KeyDecoder, KeyEncoder}

object codec {
  private implicit val roleKeyDecoder: KeyDecoder[Role] = KeyDecoder[Role](decode[Role](_).toOption)
  private implicit val roleKeyEncoder: KeyEncoder[Role] = KeyEncoder[Role](_.asJson.noSpaces)
  implicit val compositionConfigCodec: Codec[CompositionConfig] = deriveCodec
  implicit val compositionCodec: Codec[Composition] = deriveCodec
}
