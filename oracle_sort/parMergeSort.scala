import scala.annotation.tailrec
import scala.util.Random
import java.time.Duration
import java.time.LocalDate
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import scala.concurrent.duration._

def parMergeSort(list: List[Int], depth: Int = 0, max_depth: Int = 4): List[Int] = {
    @tailrec
    def merge(left: List[Int], right: List[Int], accumulator: List[Int] = List()): List[Int] = (left, right) match {
            case(left, Nil) => accumulator ++ left
            case(Nil, right) => accumulator ++ right
            case(leftHead :: leftTail, rightHead :: rightTail) =>
                if (leftHead < rightHead) merge(leftTail, right, accumulator :+ leftHead)
                else merge(left, rightTail, accumulator :+ rightHead)
        } 

    val n = list.length / 2
    if (n == 0) list
    else {
        val (left, right) = list.splitAt(n)
        
        if( depth < max_depth) {
            val lf = Future { parMergeSort(left, depth + 1, max_depth) }
            val rf = Future { parMergeSort(right, depth + 1, max_depth) }
            
            Await.result(lf, scala.concurrent.duration.Duration.Inf)
            Await.result(rf, scala.concurrent.duration.Duration.Inf)
                    
            merge( lf.value.orNull.get,  rf.value.orNull.get )  
        } else {
            merge( mergeSort(left), mergeSort(right) )
        }
    }
}

val r = new Random()
val list = (1 to 10000).map( _ => r.nextInt()).toList

//сверка правильности работы
assert(parMergeSort(list,0,0) == list.sorted)
assert(parMergeSort(list) == list.sorted)
assert(parMergeSort(list,0,0) == parMergeSort(list))

//замер времени работы 
def calcMs(c: => Unit): Long = {
    val st_dt = java.time.LocalDateTime.now
    c
    Duration.between(st_dt, java.time.LocalDateTime.now).toMillis
}

var res: List[(Int,Long)] = List()
for(i <- 0 to 8; j <- 0 to 3) {
    res = res :+ (i, calcMs({
        parMergeSort(list,0,i)
    }) )
}
res.groupBy(_._1).mapValues(i => i.map(_._2).sum / i.map(_._2).count(_=>true)).toSeq.sortBy(_._2)
//res74: Seq[(Int, Long)] = Vector((4,4534), (5,4599), (6,4608), (3,4610), (2,4657), (8,4691), (7,4702), (1,5073), (0,6697))
//sequencial sort = 6697
//parallel merge sort (4) == 4534
