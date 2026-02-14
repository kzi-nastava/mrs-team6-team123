package rs.ac.uns.ftn.asd.Projekatsiit2023.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import rs.ac.uns.ftn.asd.Projekatsiit2023.utils.VehicleTypeDeserializer;

@JsonDeserialize(using = VehicleTypeDeserializer.class)
public enum VehicleType {
    STANDARD,
    VAN,
    LUXURY
}
