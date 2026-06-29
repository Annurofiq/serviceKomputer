package com.servicekomputer.util;

public class SessionManager {
    private static String username;
    private static String role;
    private static int userId;

    public static void setSession(int id, String uname, String r) {
        userId = id;
        username = uname;
        role = r;
    }

    public static String getUsername() { return username; }
    public static String getRole() { return role; }
    public static int getUserId() { return userId; }

    public static boolean isAdmin() {
        return "Admin".equals(role);
    }

    public static void clearSession() {
        username = null;
        role = null;
        userId = 0;
    }
}
