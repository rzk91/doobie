
scalaVersion := "2.13.8"
name := "doobie-world"
organization := "rzk.scala"
version := "1.0"

libraryDependencies ++= doobieDependencies ++ otherDependencies

val DoobieVersion = "1.0.0-RC1"
val NewTypeVersion = "0.4.4"

val doobieDependencies = Seq(
  "org.tpolecat" %% "doobie-core"     % DoobieVersion,
  "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
  "org.tpolecat" %% "doobie-hikari"   % DoobieVersion,
  "io.estatico"  %% "newtype"         % NewTypeVersion
)

val otherDependencies = Seq("org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1")