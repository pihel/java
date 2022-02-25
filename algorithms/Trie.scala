class TrieNode {
  var cnt: Int = 0
  var abc: Map[Char, TrieNode] = Map()

  def dump(lvl: Int = 0, prev: String = ""): Unit = {
    for(n <- abc) {
      (0 until lvl).foreach(x=>print("."))
      println(n._1, n._2.cnt, prev + n._1)
      n._2.dump(lvl + 1, prev + n._1)
    }
  }
}

//префиксное дерево
//поиск частоты с которой встречается слово в тексте
class Trie {
  val root: TrieNode = new TrieNode()

  //вставить строку в дерево
  def insert(str: String): Unit = {
    var cur_node = root
    for(c <- str) {
      if(!cur_node.abc.contains(c)) {
        cur_node.abc = cur_node.abc ++ Map(c -> new TrieNode())
      }

      cur_node = cur_node.abc(c)
      cur_node.cnt += 1

    }
  } //insert

  //вывести дерево
  def dump()= root.dump()

  //поиск строки в дереве: найденная строка, число повторов в тексте
  def find(str: String): (String, Int) = {
    var cur_node = root
    var prev = ""
    for(c <- str) {
      prev += c
      if(!cur_node.abc.contains(c)) {
        return (prev, cur_node.cnt)
      } else {
        cur_node = cur_node.abc(c)
      }

    }
    return (prev, cur_node.cnt)
  }

  //более длинные варианты слова
  def getVars(str: String): String = {
    var cur_node = root
    var prev = ""
    for(c <- str) {
      prev += c
      if(!cur_node.abc.contains(c)) {
       return Array(prev).mkString(", ")
      } else {
        cur_node = cur_node.abc(c)
      }

    }
    cur_node.abc.map(c=> prev + c._1 ).toArray.mkString(", ")
  }



}

object ExampleTrie extends App {
  val lines = scala.io.Source.fromFile("C:/Users/007/Google Диск/info/articles/test.txt").mkString
  val tokens = lines.mkString.split(" ").filter(x=>x.size > 0).map(_.toLowerCase)

  var tr = new Trie()
  tokens.foreach(t => tr.insert(t))

  /*
  * (в,8,в)
  .(а,2,ва)
  ..(р,1,вар)
  * */
  tr.dump()

  //латы, лати
  println(tr.getVars("лат"))

  //(лат,2)
  println(tr.find("лат"))
}