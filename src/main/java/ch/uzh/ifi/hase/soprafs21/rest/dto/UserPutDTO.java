package ch.uzh.ifi.hase.soprafs21.rest.dto;

public class UserPutDTO {

    private String password;

    private String username;

    private String email;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public  void setEmail(String email) { this.email = email;}

    public String getEmail(){return email;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
