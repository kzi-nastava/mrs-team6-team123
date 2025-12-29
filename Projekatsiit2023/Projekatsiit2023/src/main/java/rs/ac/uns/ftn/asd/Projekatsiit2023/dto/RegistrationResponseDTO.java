package rs.ac.uns.ftn.asd.Projekatsiit2023.dto;

public class RegistrationResponseDTO {
    private String message;
    private Long userId;
    private String email;

    public RegistrationResponseDTO() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}