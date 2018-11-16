package io.reactivex

import io.reactivex.observers.TestObserver
import org.junit.jupiter.api.Test

internal class GasStationMarkerTest {

    @Test
    fun `create observable from array`() {
        val array = arrayOf(1, 2)

        val observable = Observable.fromArray(array).flatMapIterable { it -> it.toList() }

        TestObserver<Int>().apply {
            observable.subscribe(this)

            this.assertValueCount(2)
        }
    }

}