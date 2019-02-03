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

import android.net.Uri
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.api.places.places_api_poc.model.PlaceWrapper
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

/**
 * This class uses Roboelectric to mock [android.net.Uri] and it uses the [org.junit.Test]
 * annotation from JUnit4 (and not JUnit5). Also, @[org.junit.runner.RunWith] is JUnit4 (and not
 * JUnit5).
 *
 * Currently (as of Oct 19 2018) Roboelectric doesn't work w/ JUnit5, and also doesn't work w/
 * API level 28, which is why these changes have to be made.
 */
@RunWith(RobolectricTestRunner::class)
class PlaceWrapperTest {

    @Test
    fun `Convert valid GMS object into PlaceWrapper`() {

        val place = mockk<Place>().apply {
            every { id } returns "placeId"
            every { placeTypes } returns listOf(Place.TYPE_CAFE, Place.TYPE_CAR_DEALER)
            every { address } returns "address"
            every { locale } returns Locale("en")
            every { name } returns "name"
            every { latLng } returns LatLng(-33.880490, 151.184363)
            every { viewport } returns LatLngBounds(
                    LatLng(-33.880490, 151.184363),
                    LatLng(-33.858754, 151.229596))
            every { websiteUri } returns Uri.parse("http://google.com")
            every { phoneNumber } returns "1231231234"
            every { rating } returns 4.4f
            every { priceLevel } returns -1
            every { attributions } returns "n/a"
            every { freeze() } returns this
        }

        with(PlaceWrapper(place, 1f)) {
            assertThat(id).isEqualTo("placeId")
            assertThat(placeTypes).containsAll(
                    listOf(Place.TYPE_CAFE, Place.TYPE_CAR_DEALER))
            assertThat(address).isEqualTo("address")
            assertThat(locale).isEqualTo(Locale("en"))
            assertThat(name).isEqualTo("name")
            assertThat(latLng).isEqualTo(LatLng(-33.880490, 151.184363))
            assertThat(viewport).isEqualTo(LatLngBounds(
                    LatLng(-33.880490, 151.184363),
                    LatLng(-33.858754, 151.229596)))
            assertThat(websiteUri).isEqualTo(Uri.parse("http://google.com"))
            assertThat(phoneNumber).isEqualTo("1231231234")
            assertThat(rating).isCloseTo(4.4f, Percentage.withPercentage(1.0))
            assertThat(priceLevel).isEqualTo(-1)
            assertThat(attributions).isEqualTo("n/a")
        }

    }

    @Test
    fun `Convert incomplete GMS object into PlaceWrapper`() {

        val place = mockk<Place>().apply {
            every { id } returns "id"
            every { placeTypes } returns listOf()
            every { address } returns null
            every { locale } returns Locale("en")
            every { name } returns "name"
            every { latLng } returns LatLng(-33.880490, 151.184363)
            every { viewport } returns null
            every { websiteUri } returns Uri.parse("http://google.com")
            every { phoneNumber } returns ""
            every { rating } returns 4.4f
            every { priceLevel } returns -1
            every { attributions } returns "n/a"
            every { freeze() } returns this
        }

        with(PlaceWrapper(place, 1f)) {
            assertThat(id).isEqualTo("id")
            assertThat(placeTypes).isEmpty()
            assertThat(address).isNull()
            assertThat(locale).isEqualTo(Locale("en"))
            assertThat(name).isEqualTo("name")
            assertThat(latLng).isEqualTo(LatLng(-33.880490, 151.184363))
            assertThat(viewport).isNull()
            assertThat(websiteUri).isEqualTo(Uri.parse("http://google.com"))
            assertThat(phoneNumber).isEmpty()
            assertThat(rating).isCloseTo(4.4f, Percentage.withPercentage(1.0))
            assertThat(priceLevel).isEqualTo(-1)
            assertThat(attributions).isEqualTo("n/a")
        }

    }
}