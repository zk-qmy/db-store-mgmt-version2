package customer.reviews;

import org.bson.Document;
import org.bson.types.ObjectId;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReviewView extends JFrame {
    private final JButton btnBack = new JButton("Back");
    private final JButton btnSubmit = new JButton("Submit All");
    private final JPanel reviewPan;
    private ObjectId currentOrderID;
    private Map<ObjectId, JTextArea> reviewTextAreas = new HashMap<>();

    public ReviewView() {
        this.setTitle("Share your review on our products!");
        this.setLayout(new BorderLayout());
        this.setSize(1020, 800);
        this.setLocationRelativeTo(null);
        // LabelTit
        JLabel labelTit = new JLabel("Review Order");
        labelTit.setBorder(new EmptyBorder(20, 10, 40, 20));
        labelTit.setFont(new Font("Arial", Font.BOLD, 50));
        // Parent panel - border
        JPanel parentPan = new JPanel();
        parentPan.setLayout(new BorderLayout());
        // Control panel - border
        JPanel controlPan = new JPanel();
        controlPan.setLayout(new BoxLayout(controlPan, BoxLayout.X_AXIS));
        controlPan.setBorder(new EmptyBorder(40, 20, 40, 20));
        controlPan.add(btnBack);
        controlPan.add(btnSubmit);
        controlPan.add(btnBack);
        // Order panel - grid
        reviewPan = new JPanel();
        reviewPan.setLayout(new BoxLayout(reviewPan, BoxLayout.Y_AXIS));
        //reviewPan.setLayout(new GridLayout(0,1,10,30));
        reviewPan.setBorder(new EmptyBorder(0, 10, 30, 10));

        // Scrollable Pane
        JScrollPane scrollPan = new JScrollPane(reviewPan);
        scrollPan.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //scrollPan.setPreferredSize(new Dimension(600, 400));

        parentPan.add(controlPan, BorderLayout.SOUTH);
        parentPan.add(scrollPan, BorderLayout.CENTER);
        scrollPan.setViewportView(reviewPan);
        this.getContentPane().add(parentPan, BorderLayout.CENTER);
        this.getContentPane().add(labelTit, BorderLayout.NORTH);
    }
    public void getCurrentOrderID(ObjectId orderID){
        currentOrderID = orderID;
        System.out.println("currentOrderID: " + currentOrderID);
    }
    public void displayProductsForReview(List<Reviews> reviewByOrderList){
        reviewPan.removeAll();

        Border boxBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        if (reviewByOrderList == null || reviewByOrderList.isEmpty() ) {
            JLabel emptyList = new JLabel("No review available");
            emptyList.setAlignmentX(Component.CENTER_ALIGNMENT);
            reviewPan.add(emptyList);
        } else {
            for (Reviews review : reviewByOrderList) {
                JPanel box = new JPanel();
                box.setLayout(new GridLayout(1,3));
                box.setPreferredSize(new Dimension(0, 100));
                box.setMinimumSize(new Dimension(0, 100)); // Ensure minimum size is respected
                box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                box.setBorder(boxBorder);

                int productID = review.getProductID();
                String reviewText = review.getReviewText();
                ObjectId reviewID = review.getReviewID();

                // Product ID Label
                JLabel productIDLabel = new JLabel("Product ID: " + productID);
                productIDLabel.setFont(new Font("Arial", Font.BOLD, 18));
                // Review Text Area
                JTextArea reviewTextArea = new JTextArea();//reviewText != null ? reviewText : "Enter your review here!");
                reviewTextArea.setLineWrap(true);
                reviewTextArea.setWrapStyleWord(true);
                reviewTextArea.setEditable(true);
                reviewTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
                JScrollPane reviewScrollPane = new JScrollPane(reviewTextArea);
                reviewScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                box.add(productIDLabel);
                box.add(reviewScrollPane);

                reviewPan.add(box);
                reviewTextAreas.put(reviewID, reviewTextArea);
            }
        }
        reviewPan.revalidate();
        reviewPan.repaint();
        reviewPan.setVisible(true);
    }
    public JButton getBtnBack(){return btnBack;}
    public JButton getBtnSubmit(){return btnSubmit;}
    public ObjectId getCurrentOrderID() {
        return currentOrderID;
    }
    public Map<ObjectId, JTextArea> getReviewTextAreas() {
        return reviewTextAreas;
    }
    public void setReviewTextAreas(Map<ObjectId, JTextArea> newReviewTextAreas){
        this.reviewTextAreas = newReviewTextAreas;
    }

    public JPanel getReviewPan() {
        return reviewPan;
    }
}