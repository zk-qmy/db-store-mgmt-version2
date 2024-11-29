package orders;

import java.util.ArrayList;
import java.util.List;

public class Orders {
    private int id;
    private int cusID;
    private String status;
    private double total;
    private List<OrderDetails> lines;
    private String ctmName;
    private String address;
    private String phone;

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

    public int getCusID() {
        return cusID;
    }

    public int getOrderID() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setCusID(int cusID) {
        this.cusID = cusID;
    }

    public void setOrderId(int id) {
        this.id = id;
    }

    public void addLine (OrderDetails line) {
        lines.add(line);
    }
    public void removeLine(OrderDetails line) {
        lines.remove(line);
    }
    public List<OrderDetails> getLines() {
        return lines;
    }
}
