package com.agilogy.timetracking.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

fun sum(a: Int, b: Int): Int = a + b // if (a == 0) b - 1 else a + b

class SumTest : FunSpec() {
    init {
        test("sum") {
            val a = 3
            val b = 4
            val res = sum(a, b)
            assertEquals(7, res)
        }

        test("sum is associative") {
            // for all a, b, c: a + (b + c) = (a + b) + c
            checkAll(Arb.int(), Arb.int(), Arb.int()) { a, b, c ->
                assertEquals(sum(a, sum(b, c)), sum(sum(a, b), c))
            }
        }

        test("sum is commutative") {
            // for all a, b: a+b = b+a
            checkAll(Arb.int(), Arb.int()) { a, b ->
                assertEquals(sum(a, b), sum(b, a))
            }
        }

        test("sum has 0 as a neutral element") {
            // for all a: a + 0 = 0 + a = a
            checkAll(Arb.int()) { a ->
                assertEquals(a, sum(a, 0))
            }
        }

        test("foo") {
            checkAll(Arb.int(-1000..1000), Arb.int(1..1000)) { a, b ->
                assertTrue(sum(a, b) > a)
            }
        }
    }
}
