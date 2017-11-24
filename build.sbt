import sbt.Resolver

import sbt.Keys._
import sbt._

name := "code-inspection"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += Resolver.sonatypeRepo("public")
assemblyJarName in assembly := "code-inspection.jar"

libraryDependencies ++= Seq(
  "com.github.pureconfig"      %% "pureconfig"            % "0.8.0",
  "com.amazonaws"              % "aws-lambda-java-events" % "2.0.1",
  "com.amazonaws"              % "aws-lambda-java-core"   % "1.1.0",
  "com.github.seratch"         %% "awscala"               % "0.5.+",
  "org.zeroturnaround"         % "zt-zip"                 % "1.10",
  "com.typesafe.play"          %% "play-json"             % "2.6.7",
  "commons-lang"               % "commons-lang"           % "2.6",
  "com.amazonaws"              % "aws-lambda-java-log4j"  % "1.0.0",
  "org.slf4j"                  % "slf4j-log4j12"          % "1.7.25",
  "log4j"                      % "log4j"                  % "1.2.17",
  "com.typesafe.scala-logging" %% "scala-logging"         % "3.5.0",
  "commons-io"                 % "commons-io"             % "2.5",
  "org.scalaj"                 %% "scalaj-http"           % "2.3.0",
  "org.typelevel"              %% "cats-core"             % "0.9.0",
  "org.mockito"                % "mockito-all"            % "1.10.19" % "test",
  "com.github.tomakehurst"     % "wiremock"               % "1.52" % "test",
  "org.scalatest"              %% "scalatest"             % "3.0.4" % "test",
  "com.lihaoyi"                %% "ammonite-ops"          % "1.0.3" % "test",
  "net.sourceforge.jregex"     % "jregex"                 % "1.2_01"
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")

testOptions in Test += Tests.Argument("-oDF")
