package vn.edu.student.fooddelivery.domain.util

import kotlinx.coroutines.CancellationException

suspend inline fun <T> runSuspendCatching(crossinline block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Throwable) {
        Result.failure(error)
    }
