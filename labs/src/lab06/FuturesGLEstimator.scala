/**
 * Parallel Processing Lab 06
 * Due: 3/3/2019
 * Author: Bradley Lamitie
 * Description: An App that when run uses futures and the Gregory-Leibniz
 *             series to estimate pi.
 */

package lab06

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext,
  ExecutionContextExecutor, Future}

object FuturesGLEstimator extends App {

  val StartTime = System.nanoTime().toDouble

  /** Half the number of available processors to get physical cores. */
  val numOfCores = Runtime.getRuntime.availableProcessors
  println("N = " + numOfCores / 2)

  /** Calculate how much work is distributed amongst each core. */
  val limiter = Int.MaxValue / numOfCores/2

  var estimatedPI = 0.0

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  /** Create a future for each core to execute different parts of the series. */
  val futures = for(i <- 0 until (numOfCores / 2) - 1) yield Future{
    Thread.sleep(1000)

    /** Sum the series using (-1ⁿ / (2n+1)). */
    (i * limiter to limiter * (i + 1)).foldLeft(0.0) {
      (r, c) =>
        val coefficient = if(c % 2 == 0){
          4
        }else{
          -4
        }
        r + (coefficient / (2.0 * c + 1))
    }
  }

  /** Accumulate the result of each future. */
  futures.foreach {
    future =>
      val result = Await.result(future, Duration(100000, "millis"))
      estimatedPI = estimatedPI + result
  }

  /** Print estimated pi value and amount of time the code took to execute. */
  println("Pi = " + estimatedPI)

  /** Print amount of elapsed time since execution began. */
  val dt = (System.nanoTime() - StartTime) / 1000000000
  println("dt = " + BigDecimal(dt)
    .setScale(2, BigDecimal.RoundingMode.HALF_UP)
    .toDouble)
}
