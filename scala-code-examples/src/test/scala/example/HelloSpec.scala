package example

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class HelloSpec extends AnyFlatSpec with Matchers {
  "Hello" should "print greeting" in {
    Hello.main(Array.empty)
    succeed
  }
}
