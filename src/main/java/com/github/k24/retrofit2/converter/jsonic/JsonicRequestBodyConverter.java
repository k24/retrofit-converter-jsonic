/*
 * Copyright (C) 2016 k24
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
package com.github.k24.retrofit2.converter.jsonic;

import net.arnx.jsonic.JSON;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

final class JsonicRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private final JSON json;

    JsonicRequestBodyConverter(JSON json) {
        this.json = json;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        json.format(value, bytes);
        return RequestBody.create(MEDIA_TYPE, bytes.toByteArray());
    }
}
