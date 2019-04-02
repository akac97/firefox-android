/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.service.glean.metrics

import android.support.annotation.VisibleForTesting
import mozilla.components.service.glean.Dispatchers
import mozilla.components.service.glean.storages.DatetimesStorageEngine
import mozilla.components.service.glean.utils.parseISOTimeString
import mozilla.components.support.base.log.logger.Logger
import java.util.Calendar
import java.util.Date

/**
 * This implements the developer facing API for recording datetime metrics.
 *
 * Instances of this class type are automatically generated by the parsers at build time,
 * allowing developers to record values that were previously registered in the metrics.yaml file.
 */
data class DatetimeMetricType(
    override val disabled: Boolean,
    override val category: String,
    override val lifetime: Lifetime,
    override val name: String,
    override val sendInPings: List<String>,
    val timeUnit: TimeUnit = TimeUnit.Minute
) : CommonMetricData {

    override val defaultStorageDestinations: List<String> = listOf("metrics")

    private val logger = Logger("glean/DatetimeMetricType")

    /**
     * Set a datetime value, truncating it to the metric's resolution.
     *
     * @param value The [Date] value to set. If not provided, will record the current time.
     */
    fun set(value: Date = Date()) {
        if (!shouldRecord(logger)) {
            return
        }

        @Suppress("EXPERIMENTAL_API_USAGE")
        Dispatchers.API.launch {
            // Delegate storing the datetime to the storage engine.
            DatetimesStorageEngine.set(
                this@DatetimeMetricType,
                value
            )
        }
    }

    /**
     * Set a datetime value, truncating it to the metric's resolution.
     *
     * This is provided as an internal-only function so that we can test that timezones
     * are passed through correctly.  The normal public interface uses [Date] objects which
     * are always in the local timezone.
     *
     * @param value The [Calendar] value to set. If not provided, will record the current time.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    internal fun set(value: Calendar) {
        if (!shouldRecord(logger)) {
            return
        }

        @Suppress("EXPERIMENTAL_API_USAGE")
        Dispatchers.API.launch {
            // Delegate storing the datetime to the storage engine.
            DatetimesStorageEngine.set(
                this@DatetimeMetricType,
                value
            )
        }
    }

    /**
     * Tests whether a value is stored for the metric for testing purposes only. This function will
     * attempt to await the last task (if any) writing to the the metric's storage engine before
     * returning a value.
     *
     * @param pingName represents the name of the ping to retrieve the metric for.  Defaults
     *                 to the either the first value in [defaultStorageDestinations] or the first
     *                 value in [sendInPings]
     * @return true if metric value exists, otherwise false
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun testHasValue(pingName: String = getStorageNames().first()): Boolean {
        @Suppress("EXPERIMENTAL_API_USAGE")
        Dispatchers.API.assertInTestingMode()

        return DatetimesStorageEngine.getSnapshot(pingName, false)?.get(identifier) != null
    }

    /**
     * Returns the string representation of the stored value for testing purposes only. This
     * function will attempt to await the last task (if any) writing to the the metric's storage
     * engine before returning a value.
     *
     * @param pingName represents the name of the ping to retrieve the metric for.  Defaults
     *                 to the either the first value in [defaultStorageDestinations] or the first
     *                 value in [sendInPings]
     * @return value of the stored metric
     * @throws [NullPointerException] if no value is stored
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun testGetValueAsString(pingName: String = getStorageNames().first()): String {
        @Suppress("EXPERIMENTAL_API_USAGE")
        Dispatchers.API.assertInTestingMode()

        return DatetimesStorageEngine.getSnapshot(pingName, false)!![identifier]!!
    }

    /**
     * Returns the stored value for testing purposes only. This function will attempt to await the
     * last task (if any) writing to the the metric's storage engine before returning a value.
     *
     * [Date] objects are always in the user's local timezone offset. If you
     * care about checking that the timezone offset was set and sent correctly, use
     * [testGetValueAsString] and inspect the offset.
     *
     * @param pingName represents the name of the ping to retrieve the metric for.  Defaults
     *                 to the either the first value in [defaultStorageDestinations] or the first
     *                 value in [sendInPings]
     * @return value of the stored metric
     * @throws [NullPointerException] if no value is stored
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun testGetValue(pingName: String = getStorageNames().first()): Date {
        @Suppress("EXPERIMENTAL_API_USAGE")
        Dispatchers.API.assertInTestingMode()

        return parseISOTimeString(DatetimesStorageEngine.getSnapshot(pingName, false)!![identifier]!!)!!
    }
}
