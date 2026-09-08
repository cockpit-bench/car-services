/*
 * Copyright (C) 2026 car-services contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.car.watchdog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/** Tests UTC boundaries used by watchdog daily accounting and retention. */
public class TimeSourceTest {
    private static TimeSource at(String timestamp) {
        Instant instant = Instant.parse(timestamp);
        return new TimeSource() {
            @Override
            public Instant now() {
                return instant;
            }
        };
    }

    @Test
    public void currentDateTime_preservesInstantAndUsesUtc() {
        TimeSource source = at("2024-06-30T23:59:59.123456789Z");
        assertEquals(source.now(), source.getCurrentDateTime().toInstant());
        assertEquals(ZoneOffset.UTC, source.getCurrentDateTime().getZone());
    }

    @Test
    public void currentDate_discardsTimeAndFractionalSeconds() {
        TimeSource source = at("2024-06-30T23:59:59.999999999Z");
        assertEquals(ZonedDateTime.parse("2024-06-30T00:00:00Z"), source.getCurrentDate());
    }

    @Test
    public void currentDate_midnightStartsNextAccountingDay() {
        assertEquals(ZonedDateTime.parse("2024-06-30T00:00:00Z"),
                at("2024-06-30T23:59:59.999999999Z").getCurrentDate());
        assertEquals(ZonedDateTime.parse("2024-07-01T00:00:00Z"),
                at("2024-07-01T00:00:00Z").getCurrentDate());
    }

    @Test
    public void currentDate_keepsLeapDay() {
        assertEquals(ZonedDateTime.parse("2024-02-29T00:00:00Z"),
                at("2024-02-29T12:34:56Z").getCurrentDate());
    }

    @Test
    public void currentDate_beforeEpochFloorsToPreviousDay() {
        assertEquals(ZonedDateTime.parse("1969-12-31T00:00:00Z"),
                at("1969-12-31T23:59:59.999999999Z").getCurrentDate());
    }
}
