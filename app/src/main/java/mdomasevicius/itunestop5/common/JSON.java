package mdomasevicius.itunestop5.common;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE;
import static com.fasterxml.jackson.databind.MapperFeature.*;

public class JSON {

    public static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        .enable(ACCEPT_CASE_INSENSITIVE_ENUMS);

}
