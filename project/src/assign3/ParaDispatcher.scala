package assign3
import com.mongodb.MongoClient
import parabond.cluster._
import parascale.actor.last.{Dispatcher, Task}
import parascale.parabond.casa.MongoHelper
import parascale.parabond.util.Result
import parascale.util.getPropertyOrElse
object ParaDispatcher extends  App {
  // For initial testing on a single host, use this socket.
  // When deploying on multiple hosts, use the VM argument,
  // -Dsocket=<ip address>:9000 which points to the second
  // host.
  val socket2 = getPropertyOrElse("socket","localhost:9000")
  // This spawns a list of relay workers at the sockets
  new ParaDispatcher(List("localhost:8000", socket2))
}

class ParaDispatcher(ports: List[String]) extends Dispatcher(ports){
  override def act: Unit = {
    val dbHost: String = MongoHelper.getHost

    print(dbHost)
    val ramp = List(
//          1000,
//          2000,
//          4000,
//          8000,
//          16000,
//          32000,
//          64000,
          100000 )

    var output = "ParaBond Analysis \nBy Bradley Lamitie \n31 Apr 2019 " +
      "\nBasicNode \nWorkers: 2 \nHosts: localhost (dispatcher), " +
      "localhost (worker), " + workers(1).forwardAddr + " (worker) " +
      dbHost + " (mongo) \n   N  missed     T1      TN     R     e"
    for (rung <- ramp) {
      val t0 = System.nanoTime()
      var totalT1: Double = 0
      val numPortfolios = rung

      val portfIds = checkReset(numPortfolios, 0)

      val partitionA = Partition(0, numPortfolios / 2)
      val partitionB = Partition(numPortfolios / 2, numPortfolios)

      workers.head ! partitionA
      workers(1) ! partitionB

      var counter = 0

      while (counter < 2) {
        receive match {
          case task: Task if task.kind == Task.REPLY =>
            task.payload match {
              case message: String =>
                println("got message")
              case result: Result =>
                print("got result")
                counter += 1
                totalT1 += (result.t1 - result.t0)
            }
        }
      }

      totalT1 = totalT1 seconds

      val missedPortfolios = check(portfIds)
      val tN = (System.nanoTime()  - t0) seconds
      val r = totalT1/tN
      // I am using 2 of the computers in Donnelly, each has 4 cores, and my
      // laptop which has 4
      val numCores = 12
      val e = r/numCores
      output += "\n" + numPortfolios + "    " + missedPortfolios.size + "   " + totalT1 +
      "      " + tN + "  " + r + "  " + e

      println("DONE WITH RUNG " + numPortfolios)
    }
    println(output)
  }
}