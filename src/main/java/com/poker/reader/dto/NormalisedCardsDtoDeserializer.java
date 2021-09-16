package com.poker.reader.dto;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class NormalisedCardsDtoDeserializer extends KeyDeserializer {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public NormalisedCardsDto deserializeKey(String jsonValue, DeserializationContext deserializationContext) throws IOException {
        return mapper.readValue(jsonValue, NormalisedCardsDto.class);
    }
}
