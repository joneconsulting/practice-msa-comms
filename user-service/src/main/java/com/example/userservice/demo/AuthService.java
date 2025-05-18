package com.example.userservice.demo;

public class AuthService {
    public void login(String username) {
        UserContext.setCurrentUser(username);
        System.out.println("Logged in as: " + UserContext.getCurrentUser());
    }

    public void logout() {
        UserContext.clear();
    }

    public static void main(String[] args) {
        AuthService authService = new AuthService();

        authService.login("admin");
        authService.logout();

        authService.login("user");
        authService.logout();
    }
}
