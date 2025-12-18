package rs.ac.uns.ftn.asd.Projekatsiit2023.model;

public class User {

    private Long id;
    private String email;
    private String password;
    private String name;
    private String surname;
    private String role; // USER, DRIVER, ADMIN
    private boolean blocked;

    public User() {}
}
