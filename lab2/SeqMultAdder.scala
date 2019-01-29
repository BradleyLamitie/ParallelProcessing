package lab2
//
// Parallel Processing Lab 2
// Due: 2/4/2019
// Author: Bradley Lamitie
// Description: Create an app using forEach, filter, and foldleft functions
//

object SeqMultAdder extends App {
    val nums = List(1, 3, 4, 5, 12, 2, 7, 9, 7)
    //  val f = { n: Int => println(n) }
    def f(n: Int): Unit = println(n)
    //  nums.foreach(println(_))
    val odds = nums.filter { _%2 == 1 }
    val total = odds.foldLeft(0) { (sum, odd) => sum + 2*odd}
    println(total)
}
