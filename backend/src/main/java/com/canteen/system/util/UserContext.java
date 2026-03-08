package com.canteen.system.util;

public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Integer> ROLE = new ThreadLocal<>();
    
    public static void setCurrentUser(Long userId, String username, Integer role) {
        USER_ID.set(userId);
        USERNAME.set(username);
        ROLE.set(role);
    }
    
    public static Long getCurrentUserId() {
        return USER_ID.get();
    }
    
    public static String getCurrentUsername() {
        return USERNAME.get();
    }
    
    public static Integer getCurrentRole() {
        return ROLE.get();
    }
    
    public static Integer getCurrentUserRole() {
        return ROLE.get();
    }
    
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }
}
