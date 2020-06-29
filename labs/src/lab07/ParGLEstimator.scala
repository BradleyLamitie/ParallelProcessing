/**
  * Parallel Processing Lab 07
  * Due: 3/10/2019
  * Author: Bradley Lamitie
  * Description: An App that when run uses futures and the Gregory-Leibniz
  *             series to estimate pi using parallel collections.
  */

package lab07

object ParGLEstimator extends App{

  val numOfCores = Runtime.getRuntime.availableProcessors()
  val startTime = System.nanoTime().toDouble

  /** Calculate the value used to determine the ranges */
  val limiter = Int.MaxValue / numOfCores / 2

  /** Create a parallel collection */
  val partitions = (0L until numOfCores)

  /** Determine upper and lower bounds of each range and store them in a
    * 2-tuple.
    */
  val ranges = for(k <- partitions)yield{
    val lower: Long = k * limiter
    val upper: Long = (k + 1) * limiter

    (lower, upper)
  }

  /** Sum the series using (-1ⁿ / (2n+1)) */
  val sums = ranges.map { lowerUpper =>
    val (lower, upper) = lowerUpper
    (lower to upper).foldLeft(0.0) {
      (r, c) =>
        val coefficient = if(c % 2 == 0){
          4
        }else{
          -4
        }
        r + (coefficient / (2.0 * c + 1))
    }
  }

  /** Sum all the values */
  val estimatedPi = sums.sum

  /** Print number of cores */
  println("N = " + numOfCores / 2)

  /** Print estimated pi value and amount of time the code took to execute */
  println("Pi = " + estimatedPi)

  val dt = (System.nanoTime() - startTime) / 1000000000
  println("dt = " + BigDecimal(dt)
    .setScale(2, BigDecimal.RoundingMode.HALF_UP)
    .toDouble)
}
