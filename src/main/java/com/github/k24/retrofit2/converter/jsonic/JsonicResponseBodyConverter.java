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
import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;
import java.lang.reflect.Type;

final class JsonicResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final JSON json;
    private final Type type;

    JsonicResponseBodyConverter(JSON json, Type type) {
        this.json = json;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            return json.parse(value.charStream(), type);
        } finally {
            value.close();
        }
    }
}
