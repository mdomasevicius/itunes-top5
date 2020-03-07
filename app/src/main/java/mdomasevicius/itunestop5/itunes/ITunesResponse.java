package mdomasevicius.itunestop5.itunes;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.Nulls.SKIP;

public class ITunesResponse {

    @JsonSetter(nulls = SKIP)
    public List<ITunesResult> results = List.of();

    public static class ITunesResult {
        public WrapperType wrapperType;
        private Map<String, String> properties = new HashMap<>();

        @JsonAnySetter
        public void setProperty(String key, String value) {
            this.properties.put(key, value);
        }

        public String property(String key) {
            return properties.get(key);
        }
    }

    public enum WrapperType {
        ARTIST,
        COLLECTION,
        @JsonEnumDefaultValue
        UNKNOWN
    }
}
