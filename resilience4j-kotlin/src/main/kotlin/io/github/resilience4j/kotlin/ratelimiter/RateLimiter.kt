/*
 *
 *  Copyright 2019: Brad Newman
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package io.github.resilience4j.kotlin.ratelimiter

import io.github.resilience4j.ratelimiter.RateLimiter
import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RequestNotPermitted
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * Decorates and executes the given suspend function [block].
 *
 * If [RateLimiterConfig.timeoutDuration] is non-zero, the returned function suspends until a permission is available.
 */
suspend fun <T> RateLimiter.executeSuspendFunction(block: suspend () -> T): T {
    val waitTimeNs = reservePermission(rateLimiterConfig.timeoutDuration)
    if (waitTimeNs < 0) throw RequestNotPermitted("Request not permitted for limiter: $name")
    delay(TimeUnit.NANOSECONDS.toMillis(waitTimeNs))
    return block()
}

/**
 * Decorates the given suspend function [block] and returns it.
 *
 * If [RateLimiterConfig.timeoutDuration] is non-zero, the returned function suspends until a permission is available.
 */
fun <T> RateLimiter.decorateSuspendFunction(block: suspend () -> T): suspend () -> T = {
    executeSuspendFunction(block)
}