package assign3

import parabond.cluster.{Analysis, Partition, BasicNode}
import parascale.actor.last.{Task, Worker}
import parascale.parabond.util.{Result, Job}
import parascale.util.getPropertyOrElse

import scala.collection.GenSeq

object ParaWorker extends App {
  //a. If worker running on a single host, spawn two workers
  // else spawn one worker.
  val nhosts = getPropertyOrElse("nhosts", 1)
  //Set the node, default to basic node
  val prop =
    getPropertyOrElse("node","parabond.cluster.BasicNode")
  val clazz = Class.forName(prop)
  import parabond.cluster.Node
  val node = clazz.newInstance.asInstanceOf[Node]
  // One-port configuration
  val port1 = getPropertyOrElse("port", 8000)
  // If there is 1 host, then ports include 9000 by default
  // Otherwise, if there are two hosts in this configuration,
  // use just one port which must be specified by VM options
  val ports =
  if (nhosts == 1) List(port1, 9000) else List(port1)
  // Spawn the worker(s).
  // Note: for initial testing with a single host, "ports"
  // contains two ports. When deploying on two hosts, "ports"
  // will contain one port per host.
  for (port <- ports) {
    // Start up new worker.
    new ParaWorker(port)
  }
}

class ParaWorker(port: Int) extends Worker(port){
  import ParaWorker._

  override def act: Unit = {
    val name = getClass.getSimpleName
    while(true){
      receive match {
        case task: Task =>
          task.payload match{
            case message: String =>
              sender ! name + " READY (id=" + id + ")"

            case partition: Partition =>
              val analysis: Analysis = node analyze partition
              val results = analysis.results
              val jobs = results.seq
              val partialT1 = jobs.foldLeft(0L) { (sum, job) =>
                val t1 = job.result.t1 - job.result.t0
                sum + t1
              }
              sender ! Result(partialT1)
          }
      }
    }
  }
}