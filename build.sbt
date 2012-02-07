name := "liftedcontent-blog"

organization := "eu.sbradl"

version := "1.0.0"
 
scalaVersion := "2.9.1"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots"

scalacOptions in Compile ++= Seq(
  "-deprecation",
   "-g:vars"
)