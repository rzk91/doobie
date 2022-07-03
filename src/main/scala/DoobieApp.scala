import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor._

object DoobieApp extends IOApp {

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:myimdb",
    "postgres",
    "postgres"
  )

  def findAllActorNames: IO[List[String]] = {
    val findAllActorsQuery = sql"SELECT name FROM actors".query[String]
    val findAllActors = findAllActorsQuery.to[List]
    findAllActors.transact(xa)
  }

  def findActorById(id: Int): IO[Option[Actor]] = {
    val findActor =
      sql"SELECT id, name FROM actors WHERE id = $id"
        .query[Actor]
        .option
    findActor.transact(xa)
  }

  def saveAndGetActor(name: String): IO[Actor] = {
    val saveActor = sql"INSERT INTO actors (name) VALUES ($name)"
      .update.withUniqueGeneratedKeys[Int]("id")
    
    (for {
      id ← saveActor
      actor ← sql"SELECT id, name FROM actors WHERE id = $id"
        .query[Actor].unique
    } yield actor)
      .transact(xa)
  }

  def saveAndGetMultipleActors(actors: NonEmptyList[String]): IO[List[Actor]] = {
    val insertStmt = "INSERT INTO actors (name) VALUES (?)"
    Update[String](insertStmt)
      .updateManyWithGeneratedKeys[Actor]("id", "name")(actors.toList)
      .compile
      .toList
      .transact(xa)
  }

  def deleteActorById(id: Int): IO[Int] = {
    val deleteActor = for {
      _ ← sql"DELETE FROM movies_actors WHERE actor_id = $id".update.run
      id ← sql"DELETE FROM actors WHERE id = $id".update.run
    } yield id

    deleteActor.transact(xa)
  }

  def deleteActorByName(name: String): IO[Int] =
    (for {
      id ← sql"SELECT id FROM actors WHERE name = $name".query[Int].option
      _ ← sql"DELETE FROM movies_actors WHERE actor_id = $id".update.run
      done ← sql"DELETE FROM actors WHERE id = $id".update.run
    } yield done).transact(xa)

  override def run(args: List[String]): IO[ExitCode] = {
    findAllActorNames
    .map(println)
    .as(ExitCode.Success)
  }

  case class Actor(id: Int, name: String)

  case class Movie(id: String, title: String, year: Int, actors: List[String], director: String)

}