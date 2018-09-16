package io.methvin.orphanfinder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

object Demo {
  def test(): Unit = {
    // should not warn
    val _ = Future("hello")

    // should warn
    Future("hello")

    // should warn
    Future(throw new RuntimeException("hello")).recover {
      case e: Throwable => e.printStackTrace()
    }

    println("goodbye")
  }
}
