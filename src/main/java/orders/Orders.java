package orders;

import org.bson.types.ObjectId;
import register.users.Users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Orders {
    private ObjectId id;
    //private ObjectId cusID;
    private int userID;
    private String status;
    private double total;
    private List<OrderDetails> lines;
    private String ctmName;
    private String address;
    private String phone;
    private LocalDateTime createdTime;

    public LocalDateTime getCreatedTime(){return createdTime;}

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Orders() {
        lines = new ArrayList<>();
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setCtmName(String ctmName) {
        this.ctmName = ctmName;
    }

    public String getCtmName() {
        return ctmName;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress(){return address;}

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getUserID() {
        return userID;
    }

    public ObjectId getOrderID() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setOrderId(ObjectId id) {
        this.id = id;
    }

    public void addLine (OrderDetails line) {
        lines.add(line);
    }
    public void removeLine(OrderDetails line) {
        lines.remove(line);
    }
    public void setOrderLines(List<OrderDetails> lines){this.lines = lines;}
    public List<OrderDetails> getLines() {
        return lines;
    }
}
