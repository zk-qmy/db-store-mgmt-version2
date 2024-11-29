package register.users;

public class Users {
    private int userID;
    private String name;
    private String username;
    private String password;
    private String address;
    private String phoneNum;
    private int roleID;
    private String roleName;

    public void setRoleName(String roleName) {this.roleName = roleName;}
    public String getRoleName() {return roleName;}

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
}