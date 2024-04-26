package com.agilogy.timetracking.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import org.junit.jupiter.api.Assertions.assertEquals

fun sum(a: Int, b: Int): Int = a + b

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
            TODO()
        }

        test("sum is commutative") {
            checkAll(Arb.int(), Arb.int()) { a, b ->
                assertEquals(sum(a, b), sum(b, a))
            }
        }

        test("sum has 0 as a neutral element") {
            // for all a: a + 0 = 0 + a = a
            TODO()
        }
    }
}
