package Calculator

class Stack {
  var prev : Stack = null
  var last : Stack = null
  var value :String = null
  var count: Int = 0

  def push(s: String) = {
    if(last != null) {
      prev = last
    }
    last = new Stack
    last.value = s
    last.prev = prev
    count += 1
  }

  def pop() = {
    if (last != null) {
      val ret: String = last.value
      last = last.prev
      count -= 1
      ret
    } else {
      ""
    }
  }

  def peek(): String = {
    last.value
  }
} //Stack

object Calculator extends App {
  def getPriority(c: String): Int = {
    var ret: Int = 6
    c.charAt(0) match {
      case '(' => ret = 0
      case ')' => ret = 1
      case '+' => ret = 2
      case '-' => ret = 3
      case '*' => ret = 4
      case '/' => ret = 4
      case '^' => ret = 5
      case _ => ret = 6
    }
    ret
  }

  def getExpression(expr: String): String = {
    var opers = new Stack()
    val tokens = expr.split(" ")
    var ret: String = ""
    tokens.foreach(t => {
      if (Character.isDigit(t.charAt(0))) {
        //если число, то добавляем в результирующую строку
        ret += t + " "
      }  else if (getPriority(t) <= 5) {
        //если операция
        if(t.equals("(")) {
          //начало скобок помещаем в стек
          opers.push(t)
        } else if(t.equals(")")) {
          //когда дошли до конца скобки, то выписываем все операции в скобках
          //операции уже упорядочены в порядке приоритетов за счет условия ниже
          var op: String = opers.pop()
          while(!op.equals("(")) {
            ret += op + " "
            op = opers.pop()
          }
        } else {
          if (opers.count > 0) {
            //если в стеке уже есть операции
            if (getPriority(t) <= getPriority(opers.peek())) {
              //если пришла операция менее приоритетная
              //то приоритетная достается из стека и записывается в строку
              //т.к. более приоритетная должна выполниться первой
              ret += opers.pop() + " "
            }
          }
          //новая операция записывается в конец стека
          opers.push(t)
        }
      }
    })
    //оставшаяся операции дописываются вконец
    while (opers.count > 0) {
      ret += opers.pop() + " "
    }
    ret
  }

  //( 9 - 5 ) * 3 -> 9 5 - 3 *
  def calc(expr: String): String = {
    var result = new Stack()
    val tokens = expr.split(" ")
    //читаем слева направо
    tokens.foreach(t => {
      if (Character.isDigit(t.charAt(0))) {
        //если число - добавляем в стек
        result.push(t)
      }  else if (getPriority(t) >= 2 & getPriority(t) <= 5) {
        //если оператор, то извлекаем 2 числа из стека
        val a: Double = result.pop().toDouble
        val b: Double = result.pop().toDouble

        //т.к. последний элемент из стека выбирается первым
        // , то при выполнении операции меняем их местами
        var ret: Double = 0
        t.charAt(0) match {
          case '+' => ret = b + a
          case '-' => ret = b - a
          case '*' => ret = b * a
          case '/' => ret = b / a
          case '^' => ret = Math.pow(b, a)
        }
        //результат будет единственным первым элементом стека
        result.push(ret.toString)
      }
    })
    result.peek()
  }

  val expr = "2 * ( 3 + 4 * 5 ) * 2"
  //val expr = "2 * ( 3 + 4 ) * 2"
  //val expr = "( 9 - 5 ) * 3"
  //val expr = "2 * 3 + 4 * 2"
  println(expr)
  println(getExpression(expr))
  println(calc(getExpression(expr)))
}