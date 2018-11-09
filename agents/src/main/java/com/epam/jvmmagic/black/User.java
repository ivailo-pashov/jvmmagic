package com.epam.jvmmagic.black;

public class User {

    private String userId;
    private String password;

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", password='" + password + '\'' +
               '}';
    }
}
