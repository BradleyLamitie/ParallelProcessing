package assign1

object Exam extends App{
  val lista = List(1,2,3,4,6,8)
  val listb = lista.filter(_ % 2 == 0)
  val listc = for(n <- listb) yield n * 2
  val listd = (0 until listb.length)
  print((4 >> 0) & 1)

}
