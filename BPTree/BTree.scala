package example

/*
https://ru.wikipedia.org/wiki/Красно-чёрное_дерево
частный случай b-дерева со степенью 4:
* 3 элемента в блоке
  ** в центре черный элемент
  ** по бокам красные, даже если это пустые элементы (т.е. "Все листья (NIL) — чёрные." не выполняется)

* отличие b-дерева от b+дерева:
  ** при разделение элемент переносится наверх (не остается в листе)
  ** листы не связаны друг с другом ссылками
 */

class BTree(row_inblock: Int) {
  //структура с информацией о разделении блока: ключ уходящий наверх, левый и правый блоки, уровень
  case class Split(k: Int, l: Node, r: Node, lvl: Int)

  //лист или ветвь
  abstract class Node {
    //кол-во элементов в блоке
    var cnt: Int = 0
    //число элементов в блоке = размерность дерева -1
    val elem_inblock: Int = row_inblock - 1

    //элементы в блоке
    var keys: Array[Int] = new Array[Int](elem_inblock)
    //или дочерние блоки
    var children: Array[Node] = new Array[Node](row_inblock)
    //глубина
    var level: Int = 0
    //листовой блок
    var isLeaf: Boolean = false

    def insertNonFull(key: Int, indx: Int)

    //поиск места для нового элемента
    def find(key: Int, add_one_for_bnode: Int = 0): Int = {
      //если блок пустой или значение меньше минимального
      if(cnt < 1 | key < keys(0)) return 0

      for (k <- 0 to cnt - 1) {
        //если дошли до конца, то последний элеменент (для BNode ссылка на следующий)
        if(k+1 > elem_inblock - 1)  return k + add_one_for_bnode
        //если элмент между текущим и следующим (или следующего нет)
        if(key >= keys(k) & ( key < keys(k+1) | keys(k+1) == 0 ) ) {
          return k+1
        }
      }
      -1
    }

    //вставка элемента
    def insert(key : Int): Split = {
      //поиск элемента для вставки
      val indx: Int = find(key)

      //упрощенный вариант - проверка переполнения начинается сверху, а не снизу
      if(cnt >= elem_inblock) {
        //блок будет делиться напополам
        val mid: Int = elem_inblock / 2
        val r_num = elem_inblock - mid

        var right: Node = null
        if (this.isLeaf) {
          right = new LNode()
        } else {
          right = new BNode()
          //копируем ссыылки для BNode
          Array.copy(children, mid + 1, right.children, 0, r_num )
        }
        right.cnt = r_num - 1
        right.level = level

        //копируем элементы
        Array.copy(keys, mid + 1, right.keys, 0, right.cnt)

        cnt = mid

        //вставляем новый элемент в левый или правый блок в зависимости от положения
        if(indx < mid) {
          insertNonFull(key, indx)
        } else {
          right.insertNonFull(key, indx - mid)
        }

        //сообщаем вышестояющему блоку о разделении
        new Split(keys(mid),this,right, level)

      } else {
        //если блок еще не пусто, просто добавляем
        insertNonFull(key, indx)
        null
      }

    }
    def exists(key: Int): Boolean = false

  }

  //класс ветви
  class BNode extends Node {
    isLeaf = false

    //вставка в недозаполненную ветвь
    def insertNonFull(key: Int, indx: Int) = {
      //рекурсивно вызываем вставку для дочернего блока
      val ret: Split = children(indx).insert(key)

      //если дочерний блок разделился
      if(ret != null) {
        //всавка в середину
        if (indx <= cnt) {
          //то смещаем элементы и дочерние блоки
          Array.copy(keys, indx, keys, indx + 1, cnt - indx)
          Array.copy(children, indx, children, indx + 1, cnt - indx + 1)
        }

        //заполняем значение и ссылками на разделенные блоки
        children(indx) = ret.l
        children(indx + 1) = ret.r
        keys(indx) = ret.k
        cnt += 1
      }
    }

    override def toString(): String = {
      var pad = ""
      var ret = ""
      for(j <- 0 to level - 1) pad += " . "

      //для каждого элемента в блоке
      for (i <- 0 to cnt) {
        //спускаемся рекурсивно до конца
        ret += children(i).toString()

        //выводим элемент и поднимаемся обратно по рекурсии
        if(i < cnt ) ret += pad + " " + keys(i) + "\n"
      }
      ret
    }

    override def exists(key: Int): Boolean = {
      //в блоке ветви тоже есть данные, так что предварительно проверяем их
      for(e <- 0 to cnt - 1 ) {
        if (keys(e) == key) {
          return true
        }
      }
      //иначае спускаемся вниз в найденном блоке
      val indx: Int = find(key, 1)
      return children(indx).exists(key)

    }
  } //BNode

  class LNode extends Node {
    isLeaf = true

    //вставка в неполный блок
    def insertNonFull(key: Int, indx: Int) = {
      //смещаем элементы
      if(keys(indx) != 0) {
        Array.copy(keys, indx, keys, indx + 1, cnt - indx)
      }
      keys(indx) = key
      cnt += 1
    }

    override def toString: String = {
      var ret: String = ""
      for (k <- 0 to cnt - 2) {
        ret +=  keys(k) + "\n"
      }
      ret +=  keys(cnt-1)
      ret + "\n"
    }

    override def exists(key: Int): Boolean = {
      for(e <- 0 to cnt - 1 ) if (keys(e) == key) return true
      false
    }
  } //LNode

  //корень дерева
  var root: Node = new LNode()

  def insert(key : Int) = {
    val ret: Split = root.insert(key)
    if (ret != null) {
      //разделяем корень на 2 части
      //создаем новый корень с сылками на лево и право
      val _root: BNode = new BNode()
      _root.cnt = 1
      _root.keys(0) = ret.k
      _root.children(0) = ret.l
      _root.children(1) = ret.r
      _root.level = ret.lvl + 1

      root = _root
    }
  }

  def exists(key: Int): Boolean = {
    root.exists(key)
  }

  override def toString: String = {
    root.toString()
  }
}

object Example extends App {
  var bt = new BTree(4)
  bt.insert(10)
  bt.insert(20)
  bt.insert(30)
  bt.insert(40)
  bt.insert(50)
  bt.insert(60)
  bt.insert(70)
  bt.insert(80)
  bt.insert(90)
  bt.insert(100)
  bt.insert(5)
  bt.insert(110)
  bt.insert(120)
  println(bt)
  

  println(bt.exists(5))
  println(bt.exists(120))
  println(bt.exists(30))
  println(bt.exists(60))
  println(bt.exists(40))
  println(bt.exists(-1))
  println(bt.exists(45))
  
  /*
5
10
 .  20
30
 .  .  40
50
 .  60
70
 .  80
90
 .  100
110
120

true
true
true
true
true
false
false
  */
}