package rs.ac.uns.ftn.asd.Projekatsiit2023.dto;

public class ActiveVehicleDTO {
    private Long vehicleId;
    private String location;
    private boolean available;

    public ActiveVehicleDTO() {}

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
