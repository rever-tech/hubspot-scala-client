package hubspot.util

import scala.annotation.tailrec

/**
 * Created by phuonglam on 2/13/17.
 **/
object Random {
  // 1 - a 'normal' java-esque approach
  def randomString(length: Int) = {
    val r = new scala.util.Random
    val sb = new StringBuilder
    for (i <- 1 to length) {
      sb.append(r.nextPrintableChar)
    }
    sb.toString
  }

  // 2 - similar to #1, but using an array
  def randomStringArray(length: Int) = {
    val r = new scala.util.Random
    val a = new Array[Char](length)
    val sb = new StringBuilder
    for (i <- 0 to length-1) {
      a(i) = r.nextPrintableChar
    }
    a.mkString
  }

  // 3 - recursive, but not tail-recursive
  def randomStringRecursive(n: Int): List[Char] = {
    n match {
      case 1 => List(util.Random.nextPrintableChar)
      case _ => List(util.Random.nextPrintableChar) ++ randomStringRecursive(n-1)
    }
  }

  // 3b - recursive, but not tail-recursive
  def randomStringRecursive2(n: Int): String = {
    n match {
      case 1 => util.Random.nextPrintableChar.toString
      case _ => util.Random.nextPrintableChar.toString ++ randomStringRecursive2(n-1).toString
    }
  }

  // 4 - tail recursive, no wrapper
  @tailrec
  def randomStringTailRecursive(n: Int, list: List[Char]):List[Char] = {
    if (n == 1) util.Random.nextPrintableChar :: list
    else randomStringTailRecursive(n-1, util.Random.nextPrintableChar :: list)
  }

  // 5 - a wrapper around the tail-recursive approach
  def randomStringRecursive2Wrapper(n: Int): String = {
    randomStringTailRecursive(n, Nil).mkString
  }

  // 6 - random alphanumeric
  def randomAlphaNumericString(length: Int): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')
    randomStringFromCharList(length, chars)
  }

  // 7 - random alpha
  def randomAlpha(length: Int): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z')
    randomStringFromCharList(length, chars)
  }

  // used by #6 and #7
  def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      val randomNum = util.Random.nextInt(chars.length)
      sb.append(chars(randomNum))
    }
    sb.toString
  }

}
