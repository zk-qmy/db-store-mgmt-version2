package register;
import register.users.Users;

public class Session {
    private static Session instance;
    private Users currentUser;

    private Session() {
    }

    public static synchronized Session getInstance() {
        if (instance==null) {
            instance = new Session();
        }
        return instance;
    }

    public void setCurrentUser(Users currentUser) {this.currentUser = currentUser;}

    public Users getCurrentUser() {
        return currentUser;
    }
    public void clearSession() {
        currentUser = null;
    }
}