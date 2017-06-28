package chappy.interfaces.rest;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime dateTime, JsonGenerator jg, 
            SerializerProvider sp) throws IOException, JsonProcessingException {
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);
        jg.writeString(DateTimeFormatter.ISO_INSTANT.format(instant));
    } 
}