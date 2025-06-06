import zio.json.*

@jsonDiscriminator("@type")
sealed trait Event derives JsonCodec

case class Meetup(
  name: String,
  year: Int
) extends Event derives JsonCodec

case class Conference(
  name: String,
  year: Int,
  location: String,
  attendees: List[String]
) extends Event derives JsonCodec

val sunnytech = Conference(
  name = "SunnyTech",
  year = 2023,
  location = "Montpellier, Occitanie, France",
  attendees = List("Alice", "Bob", "Charlie")
)

val scalaio = Conference(
  name = "ScalaIO",
  year = 2023,
  location = "Lyon, Auvergne-Rhône-Alpes, France",
  attendees = List("Alice", "Bob", "Charlie")
)

val flint = Meetup(
  name = "Flint",
  year = 2023
)

//given JsonCodec[Conference] = JsonCodec.derived

@main
def main =
  println(" ")
  println(sunnytech.toJsonPretty)
  println(List(sunnytech, flint, scalaio).toJsonPretty)
  println(" ")
