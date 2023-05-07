package com.agilogy.json

import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals

class JsonTest : FunSpec() {
    init {
        test("null") {
            assertEquals(JsonNull, null.json)
        }
        test("boolean") {
            assertEquals(JsonPrimitive(true), true.json)
            assertEquals(JsonPrimitive(false), false.json)
        }

        test("number") {
            assertEquals(JsonPrimitive(0.toByte()), 0.toByte().json)
            assertEquals(JsonPrimitive(1.toShort()), 1.toShort().json)
            assertEquals(JsonPrimitive(2), 2.json)
            assertEquals(JsonPrimitive(3L), 3L.json)
            assertEquals(JsonPrimitive(4.0f), 4.0f.json)
            assertEquals(JsonPrimitive(5.0), 5.0.json)
        }

        test("string") {
            assertEquals(JsonPrimitive("foo"), "foo".json)
        }

        test("array") {
            val expected = JsonArray(listOf(JsonPrimitive(1), JsonPrimitive(2)))
            assertEquals(expected, jsonArray(1.json, 2.json))
            assertEquals(expected, listOf(1.json, 2.json).json)
        }

        test("object") {
            val expected = JsonObject(mapOf("a" to JsonPrimitive(1), "b" to JsonPrimitive(2), "d" to JsonNull))
            assertEquals(expected, jsonObject("a" to 1.json, "b" to 2.json, "c" to null, "d" to null.json))
            assertEquals(expected, jsonObject(listOf("a" to 1.json, "b" to 2.json, "c" to null, "d" to null.json)))
        }
    }
}
