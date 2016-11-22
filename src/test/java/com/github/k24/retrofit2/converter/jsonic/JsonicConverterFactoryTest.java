package com.github.k24.retrofit2.converter.jsonic;

import net.arnx.jsonic.JSON;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by k24 on 2016/11/22.
 */
public class JsonicConverterFactoryTest {
    interface AnInterface {
        String getName();
    }

    static class AnImplementation implements AnInterface {
        private String theName;

        AnImplementation() {
        }

        AnImplementation(String name) {
            theName = name;
        }

        @Override
        public String getName() {
            return theName;
        }
    }

    interface Service {
        @POST("/")
        Call<AnImplementation> anImplementation(@Body AnImplementation impl);

        @POST("/")
        Call<AnInterface> anInterface(@Body AnInterface impl);
    }

    @Rule
    public final MockWebServer server = new MockWebServer();

    private Service service;

    @Before
    public void setUp() {
        JSON json = new JSON() {
            @Override
            protected <T> T postparse(Context context, Object value, Class<? extends T> cls, Type type) throws Exception {
                if (AnInterface.class.isAssignableFrom(cls)) {
                    if (AnImplementation.class.isAssignableFrom(cls)) {
                        Object name = ((Map) value).get("theName");
                        return cls.cast(new AnImplementation(String.valueOf(name)));
                    }

                    final Object name;
                    if (value instanceof Map) {
                        name = ((Map) value).get("name");
                    } else {
                        name = null;
                    }
                    return cls.cast(new AnInterface() {
                        @Override
                        public String getName() {
                            return String.valueOf(name);
                        }
                    });
                }
                return super.postparse(context, value, cls, type);
            }
        };

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(JsonicConverterFactory.create(json))
                .build();
        service = retrofit.create(Service.class);
    }

    @Test
    public void anInterface() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"name\":\"value\"}"));

        Call<AnInterface> call = service.anInterface(new AnImplementation("value"));
        Response<AnInterface> response = call.execute();
        AnInterface body = response.body();
        assertThat(body.getName()).isEqualTo("value");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"name\":\"value\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }

    @Test
    public void anImplementation() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().setBody("{\"theName\":\"value\"}"));

        Call<AnImplementation> call = service.anImplementation(new AnImplementation("value"));
        Response<AnImplementation> response = call.execute();
        AnImplementation body = response.body();
        assertThat(body.theName).isEqualTo("value");

        RecordedRequest request = server.takeRequest();
        // TODO figure out how to get Jackson to stop using AnInterface's serializer here.
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"name\":\"value\"}");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
    }
}