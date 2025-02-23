package customer.cart;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CartView extends JFrame {

    private JButton btnAdd = new JButton("Add a new item");
    private JButton btnOrder = new JButton("Place Order");

    private DefaultTableModel items = new DefaultTableModel();

    private JTable tblItems = new JTable(items);
    private JLabel labTotal = new JLabel("Total: ");

    public CartView() {

        this.setTitle("Cart");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setSize(400, 450);


        items.addColumn("Product ID");
        items.addColumn("Name");
        items.addColumn("Price");
        items.addColumn("Quantity");
        items.addColumn("Cost");

        JPanel panelOrder = new JPanel();
        panelOrder.setPreferredSize(new Dimension(400, 450));
        panelOrder.setLayout(new BoxLayout(panelOrder, BoxLayout.PAGE_AXIS));
        tblItems.setBounds(0, 0, 400, 350);
        panelOrder.add(tblItems.getTableHeader());
        panelOrder.add(tblItems);
        panelOrder.add(labTotal);
        tblItems.setFillsViewportHeight(true);
        this.getContentPane().add(panelOrder);

        JPanel panelButton = new JPanel();
        panelButton.setPreferredSize(new Dimension(400, 100));
        panelButton.add(btnAdd);
        panelButton.add(btnOrder);
        this.getContentPane().add(panelButton);

    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnOrder() {
        return btnOrder;
    }

    public JLabel getLabTotal() {
        return labTotal;
    }

    public void addRow(Object[] row) {
        items.addRow(row);
    }
    public void clearCartTable(){
        DefaultTableModel model = (DefaultTableModel) tblItems.getModel();
        model.setRowCount(0);
    }
}
