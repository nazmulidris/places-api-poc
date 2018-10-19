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

import com.google.api.places.places_api_poc.model.AutocompletePredictionData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AutocompletePredictionDataTest {

    @Test
    fun `some test that is run`() {
        val a = "hi"
        val b = a
        assertThat(a).isEqualTo(b)
    }

    @Test
    fun `some test on data class`() {
        val data = AutocompletePredictionData(fullText ="fullText",
                                              primaryText = "primaryText",
                                              secondaryText = "secondaryText",
                                              placeId = null,
                                              placeTypes = null
                                              )
        assertThat(data.fullText).isNotEmpty()
        assertThat(data.placeId).isNull()
    }

}