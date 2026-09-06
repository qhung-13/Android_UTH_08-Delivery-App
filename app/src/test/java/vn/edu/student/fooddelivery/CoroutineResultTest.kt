package vn.edu.student.fooddelivery

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test
import vn.edu.student.fooddelivery.domain.util.runSuspendCatching

class CoroutineResultTest {
    @Test fun `cancellation is rethrown instead of converted to failure`() {
        assertThrows(CancellationException::class.java) {
            runBlocking {
                runSuspendCatching<Unit> { throw CancellationException("cancelled") }
            }
        }
    }
}
