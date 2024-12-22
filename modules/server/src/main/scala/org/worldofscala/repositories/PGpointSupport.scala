package org.worldofscala.repositories

import io.getquill.*
import org.postgresql.geometric.PGpoint
import org.worldofscala.organisation.LatLon
import io.getquill.jdbczio.Quill

trait PGpointSupport {
  val quill: Quill.Postgres[SnakeCase] // your context should go here

  import quill.*

  given Decoder[LatLon] =
    decoder((index, row, _) =>
      val obj = row.getObject(index)
      if (row.wasNull) {
        LatLon.empty
      } else {
        val point = obj.asInstanceOf[PGpoint]
        LatLon(point.x, point.y)
      }
    )
  given Encoder[LatLon] =
    encoder(
      java.sql.Types.OTHER,
      (index, value, row) => row.setObject(index, value, java.sql.Types.OTHER)
    ) // database-specific implementation

  // Only for postgres
  implicit def arrayLatLonEncoder[Col <: Seq[LatLon]]: Encoder[Col] = arrayRawEncoder[LatLon, Col]("PGpoint")
  implicit def arrayLatLonDecoder[Col <: Seq[LatLon]](implicit bf: CBF[LatLon, Col]): Decoder[Col] =
    arrayRawDecoder[LatLon, Col]
}
