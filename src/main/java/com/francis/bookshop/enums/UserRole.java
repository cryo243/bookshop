package com.francis.bookshop.enums;

public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }
    public static boolean isValidRole(String input) {
        for (UserRole role : UserRole.values()) {
            if (role.role.equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }
}
