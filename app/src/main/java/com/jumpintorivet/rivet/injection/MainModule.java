package com.jumpintorivet.rivet.injection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jumpintorivet.rivet.application.MyApplication;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class MainModule {
    private final MyApplication application;

    public MainModule(MyApplication application) {
        this.application = application;
    }


    //General Injections

    @Provides
    @Singleton
    @ForApplication
    MyApplication provideApplicationContext() {
        return application;
    }


    //Otto Injections

    @Provides
    @Singleton
    Bus provideBus() {
        return new Bus();
    }


    //Gson

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Void.class, new VoidSerializer())
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }


    //Date Format

    @Provides
    @Singleton
    SimpleDateFormat provideDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }


    private static class DateSerializer implements JsonDeserializer {
        @Override
        public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                return f.parse(json.getAsString());
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static class VoidSerializer extends TypeAdapter {
        @Override
        public void write(JsonWriter out, Object value) throws IOException {
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            return null;
        }
    }
}
