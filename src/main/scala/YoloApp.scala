import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.util.transactor.Transactor._

object YoloApp extends App {

  import cats.effect.unsafe.implicits.global

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:myimdb",
    "postgres",
    "postgres"
  )

  val y = xa.yolo
  import y._

  val query = sql"SELECT name FROM actors".query[String].to[List]
  query.quick.unsafeRunSync()
}
