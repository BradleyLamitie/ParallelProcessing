/**
 * Parallel Processing Lab 5
 * Due: 2/24/2019
 * Author: Bradley Lamitie
 * Description: An App that when run uses the Gregory-Leibniz series to estimate
 *              pi.
*/

package lab05

object GLEstimator extends App {
  val N : Int = 0
  val StartTime = System.nanoTime().toDouble

  /** Sum the series using (-1ⁿ / (2n+1)) */
  val EstimatedPi = (0 to Int.MaxValue).foldLeft(0.0) {
    (r, c) =>
      val coefficient = if(c % 2 == 0){
        4
      }else{
        -4
      }
      r + (coefficient / (2.0 * c + 1))
  }

  /** Print estimated pi value and amount of time the code took to execute */
  println("Pi = " + EstimatedPi)

  val dt = (System.nanoTime() - StartTime) / 1000000000
  println("dt = " + BigDecimal(dt)
    .setScale(2, BigDecimal.RoundingMode.HALF_UP)
    .toDouble)
}
