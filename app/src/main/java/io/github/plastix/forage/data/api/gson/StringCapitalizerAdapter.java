package io.github.plastix.forage.data.api.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import javax.inject.Inject;

import io.github.plastix.forage.util.StringUtils;

/**
 * Gson adapter for automatically capitalizing a String input.
 */
public class StringCapitalizerAdapter extends TypeAdapter<String> {

    /**
     * Single @Inject annotated constructor so Dagger knows how to instantiate the adapter.
     */
    @Inject
    public StringCapitalizerAdapter() {
    }

    @Override
    public void write(JsonWriter out, String value) throws IOException {
        // Nothing
    }

    @Override
    public String read(JsonReader in) throws IOException {
        String raw = in.nextString();
        if (raw != null) {
            return StringUtils.capitalize(raw);
        } else {
            return null;
        }
    }
}
