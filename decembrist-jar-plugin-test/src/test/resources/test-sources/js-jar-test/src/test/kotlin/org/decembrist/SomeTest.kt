package org.decembrist

import kotlin.test.*

class SomeTest {

    @Test
    fun someTest(){
        throw AssertionError("some error")
    }
}