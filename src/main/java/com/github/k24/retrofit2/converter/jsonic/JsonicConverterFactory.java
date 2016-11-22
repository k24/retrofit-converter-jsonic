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
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A {@linkplain Converter.Factory converter} which uses JSONIC.
 * <p>
 * Because JSONIC is simple and having many functions to decode/encode JSON.
 * If you are mixing JSON serialization with something else (such as protocol
 * buffers), you must {@linkplain Retrofit.Builder#addConverterFactory(Converter.Factory) add this
 * instance} last to allow the other converters a chance to see their types.
 */
public class JsonicConverterFactory extends Converter.Factory {

    /** Create an instance using a default {@link JSON} instance for conversion. */
    public static JsonicConverterFactory create() {
        return create(new JSON());
    }

    /** Create an instance using {@code json} for conversion. */
    public static JsonicConverterFactory create(JSON json) {
        return new JsonicConverterFactory(json);
    }

    private final JSON json;

    private JsonicConverterFactory(JSON json) {
        this.json = json;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new JsonicResponseBodyConverter<>(json, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new JsonicRequestBodyConverter<>(json);
    }
}
