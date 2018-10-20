/*
 * Copyright 2018 Nazmul Idris. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.android.gms.location.places.AutocompletePrediction
import com.google.api.places.places_api_poc.model.parse
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * This class uses Roboelectric to mock [android.util.Log.i] and it uses the [org.junit.Test]
 * annotation from JUnit4 (and not JUnit5). Also, @[org.junit.runner.RunWith] is JUnit4 (and not
 * JUnit5).
 *
 * Currently (as of Oct 19 2018) Roboelectric doesn't work w/ JUnit5, and also doesn't work w/
 * API level 28, which is why these changes have to be made.
 */
@RunWith(RobolectricTestRunner::class)
class AutocompletePredictionDataTest {

    @Test
    fun `Parse with non null and non empty fields from GMS object`() {
        val gmsObject: AutocompletePrediction =
                mockk<AutocompletePrediction>().apply {
                    every { getFullText(null) } returns "fullText"
                    every { getPrimaryText(null) } returns "primaryText"
                    every { getSecondaryText(null) } returns "secondaryText"
                    every { placeId } returns "placeId"
                    every { placeTypes } returns listOf()
                }

        with(gmsObject.parse()) {
            assertThat(fullText).isNotBlank()
            assertThat(primaryText).isNotBlank()
            assertThat(secondaryText).isNotBlank()
            assertThat(placeId).isNotBlank()
            assertThat(placeTypes).isNotNull
        }
    }

    @Test
    fun `Parse with some null or empty fields from GMS object`() {
        val gmsObject: AutocompletePrediction =
                mockk<AutocompletePrediction>().apply {
                    every { getFullText(null) } returns "fullText"
                    every { getPrimaryText(null) } returns null
                    every { getSecondaryText(null) } returns ""
                    every { placeId } returns null
                    every { placeTypes } returns null
                }

        with(gmsObject.parse()) {
            assertThat(fullText).isNotBlank()
            assertThat(primaryText).isBlank()
            assertThat(secondaryText).isBlank()
            assertThat(placeId).isBlank()
            assertThat(placeTypes).isEmpty()
        }
    }

}