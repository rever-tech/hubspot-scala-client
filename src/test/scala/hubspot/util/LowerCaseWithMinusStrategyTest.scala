package hubspot.util

import org.junit.Test

/**
 * Created by phuonglam on 2/13/17.
 **/
case class E(camelField: String, PascalField: String) {
  def ==(e: E) = {
    if (!this.camelField.equals(e.camelField)) false
    else if (!this.PascalField.equals(e.PascalField)) false
    else true
  }
}

class LowerCaseWithMinusStrategyTest {

  @Test
  def run(): Unit = {
    val t = E("camelField", "PascalField")
    val t2 = JsonUtils.toJson(t)
    println(t2)
    println()
    println(JsonUtils.readTree(t2))
    println()
    val e = JsonUtils.fromJson[E](t2)
    assert(e == t)
    println(e)
  }
}
