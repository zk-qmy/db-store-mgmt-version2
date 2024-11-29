package admin.ordersmgmt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdOrdersView extends JFrame {
    private JButton btnRefresh = new JButton("Refresh page");
    private JButton btnAdOrder = new JButton("Add Order");
    private JButton btnUpdateOrder = new JButton("Update Order Status");
    private JButton btnDeleteOrder = new JButton("Delete Order");
    private JButton btnBack = new JButton("Back");
    private DefaultTableModel tableModel;
    private JTable table;

    public AdOrdersView() {
        this.setTitle("Order Management");
        this.setSize(1020, 800);
        this.setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JLabel labelTit = new JLabel("Order Management");
        labelTit.setBorder(new EmptyBorder(20, 10, 40, 20));
        labelTit.setFont(new Font("Arial", Font.BOLD, 50));

        //table panel
        JPanel tablePan = new JPanel();
        tablePan.setLayout(new BoxLayout(tablePan, BoxLayout.Y_AXIS));
        // Add table to scroll
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "CustomerID","Name", "Total", "Status","Address", "Phone"},0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);

        /*JScrollPane scrollPane = new JScrollPane(table);
        tablePan.add(scrollPane);*/

        JScrollPane scrollPan = new JScrollPane(table);
        scrollPan.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tablePan.add(scrollPan, BorderLayout.CENTER);
        scrollPan.setViewportView(table);

        this.add(tablePan, BorderLayout.CENTER);
        // control panel

        JPanel controlPan = new JPanel();
        controlPan.setLayout(new BoxLayout(controlPan, BoxLayout.Y_AXIS));
        controlPan.setBorder(new EmptyBorder(40, 20, 40, 20));

        // Center the buttons and add them to the control panel
        btnAdOrder.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpdateOrder.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDeleteOrder.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRefresh.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to control panel
        controlPan.add(Box.createVerticalStrut(10));
        controlPan.add(btnRefresh);
        controlPan.add(Box.createVerticalStrut(10)); // Spacer
        controlPan.add(btnAdOrder);
        controlPan.add(Box.createVerticalStrut(10)); // Spacer
        controlPan.add(btnUpdateOrder);
        controlPan.add(Box.createVerticalStrut(10)); // Spacer
        controlPan.add(btnDeleteOrder);
        controlPan.add(Box.createVerticalStrut(10)); // Spacer
        controlPan.add(btnBack);
        this.add(controlPan, BorderLayout.EAST);
        this.getContentPane().add(labelTit, BorderLayout.NORTH);
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JButton getBtnAddOrder() {
        return btnAdOrder;
    }

    public JButton getBtnUpdateOrder() {
        return btnUpdateOrder;
    }

    public JButton getBtnDeleteOrder() {
        return btnDeleteOrder;
    }

    public JButton getBtnBack(){
        return btnBack;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

}