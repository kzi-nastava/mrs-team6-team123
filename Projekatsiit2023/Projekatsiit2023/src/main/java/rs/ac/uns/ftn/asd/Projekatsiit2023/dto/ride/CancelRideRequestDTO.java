package rs.ac.uns.ftn.asd.Projekatsiit2023.dto.ride;

public class CancelRideRequestDTO {
    private Long userId;
    private String reason;

    public CancelRideRequestDTO() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}