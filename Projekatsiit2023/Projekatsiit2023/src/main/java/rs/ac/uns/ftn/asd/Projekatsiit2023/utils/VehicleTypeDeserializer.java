package rs.ac.uns.ftn.asd.Projekatsiit2023.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import rs.ac.uns.ftn.asd.Projekatsiit2023.enums.VehicleType;

import java.io.IOException;

public class VehicleTypeDeserializer extends JsonDeserializer<VehicleType> {
    @Override
    public VehicleType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return VehicleType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid vehicle type: " + value, e);
        }
    }
}
