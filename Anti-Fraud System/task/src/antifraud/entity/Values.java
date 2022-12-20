package antifraud.entity;

public class Values {
    volatile private static boolean ADMIN_REGISTERED = false;

    public static boolean isAdminRegistered() {
        return ADMIN_REGISTERED;
    }

    public static void setAdminRegistered(boolean isAdminRegistered) {
        ADMIN_REGISTERED = isAdminRegistered;
    }
}
