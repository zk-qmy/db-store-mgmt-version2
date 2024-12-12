package admin.ordersmgmt;

import register.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdOrdersView extends JFrame {
    private JButton btnRefresh = new JButton("Refresh page");
    private JButton btnAdOrder = new JButton("Add Order");
    private JButton btnUpdateOrder = new JButton("Update Order Status");
    private JButton btnDeleteOrder = new JButton("Delete Order");
    private JButton btnBack = new JButton("Back");
    private JButton btnLogOut = new JButton("LogOut");
    private DefaultTableModel tableModel;
    private JTable table;
    private JPanel controlPan;

    public AdOrdersView() {
        System.out.println("INIT ADORDERSVIEW SCREEN");
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
        addToolTip(table);

        /*JScrollPane scrollPane = new JScrollPane(table);
        tablePan.add(scrollPane);*/

        JScrollPane scrollPan = new JScrollPane(table);
        scrollPan.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tablePan.add(scrollPan, BorderLayout.CENTER);
        scrollPan.setViewportView(table);

        this.add(tablePan, BorderLayout.CENTER);
        // control panel

        controlPan = new JPanel();
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

        controlPan.add(Box.createVerticalGlue());
        controlPan.add(btnLogOut);


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
    public JButton getBtnLogOut(){
        return btnLogOut;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
    private void addToolTip(JTable table){
        // Enable tooltip for the table
        this.table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = AdOrdersView.this.table.rowAtPoint(e.getPoint());
                int column = AdOrdersView.this.table.columnAtPoint(e.getPoint());

                if (column == 0 && row != -1) { // Only show tooltip for ID column
                    Object value = AdOrdersView.this.table.getValueAt(row, column);
                    if (value != null) {
                        AdOrdersView.this.table.setToolTipText("Click to copy: " + value.toString());
                    } else {
                        AdOrdersView.this.table.setToolTipText(null);
                    }
                } else {
                    AdOrdersView.this.table.setToolTipText(null);
                }
            }
        });

        // Add mouse listener for copying on click
        this.table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = AdOrdersView.this.table.rowAtPoint(e.getPoint());
                int column = AdOrdersView.this.table.columnAtPoint(e.getPoint());

                if (column == 0 && row != -1) { // Only handle clicks on the ID column
                    Object value = AdOrdersView.this.table.getValueAt(row, column);
                    if (value != null) {
                        // Copy the value to the clipboard
                        StringSelection stringSelection = new StringSelection(value.toString());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

                        // Show a confirmation dialog
                        JOptionPane.showMessageDialog(null, "Copied to clipboard: " + value, "Copied", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });
    }
    public JPanel getControlPan(){
        return controlPan;
    }
}