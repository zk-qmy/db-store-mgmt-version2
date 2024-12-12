package customer.reviews;

import app.App;
import org.bson.Document;
import org.bson.types.ObjectId;
import register.Session;
import register.users.Users;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewController implements ActionListener {
    private ReviewView view;
    private ReviewDAO dao;

    public ReviewController(ReviewView view, ReviewDAO dao) {
        System.out.println("ReviewController init!");
        this.view = view;
        this.dao = dao;
        loadReviewByOrderList();
        view.getBtnBack().addActionListener(this);
        view.getBtnSubmit().addActionListener(this);

    }
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == view.getBtnBack()){
            view.dispose();
            System.out.println("back button in review triggered!");
            App.getInstance().getOrderHisView().setVisible(true);
        } else if (e.getSource() == view.getBtnSubmit()){
            storeReview();
        }
    }
    public boolean storeReview(){ // TO DO: fix reset map
        Map<ObjectId, String> collectedReviews = new HashMap<>();
        for (Map.Entry<ObjectId, JTextArea> entry : view.getReviewTextAreas().entrySet()) {
            //int productID = entry.getKey();
            String reviewText = entry.getValue().getText();
            ObjectId reviewID = entry.getKey();
            collectedReviews.put(reviewID, reviewText);
        }
        // if any individual review is empty or invalid
        for (String reviewText : collectedReviews.values()) {
            System.out.println(reviewText);
            if (reviewText == null || reviewText.trim().isEmpty() || reviewText.equals("Enter your review here!")) {
                JOptionPane.showMessageDialog(null, "Some reviews are empty. Please provide review text.");
                return false;
            }
        }

        if (dao.updateReviews(collectedReviews)) {
            JOptionPane.showMessageDialog(null, "Reviews successfully updated!");
            // reintialise the map
            collectedReviews.clear();
            // Clear the previous map and reset it with a new empty map
            view.getReviewTextAreas().clear(); // Clear the map
            // Optionally re-initialize the map with a new empty map
            Map<ObjectId, JTextArea> newReviewTextAreas = new HashMap<>();
            printReviewMap(collectedReviews);
            view.setReviewTextAreas(newReviewTextAreas); // Set the new empty map
            view.getReviewPan().revalidate();
            view.getReviewPan().repaint();
            return true;
        }
        return false;
    }
    // debug
    public void printReviewMap(Map<ObjectId, String> reviewMap) {
        // Check if the map is empty
        if (reviewMap == null || reviewMap.isEmpty()) {
            System.out.println("The map is empty.");
            return;
        }

        // Iterate over the map entries and print the key-value pairs
        for (Map.Entry<ObjectId, String> entry : reviewMap.entrySet()) {
            ObjectId reviewID = entry.getKey();
            String reviewText = entry.getValue();

            // Print out the key-value pair
            System.out.println("Review ID: " + reviewID.toString() + ", Review Text: " + reviewText);
        }
    }
    //


    public void loadReviewByOrderList(){
        ObjectId orderID = view.getCurrentOrderID();
        int userID = Session.getInstance().getCurrentUser().getUserID();
        System.out.println("orderID: " + orderID);

        List<Reviews> reviewByOrderList = null;
        try{
            // debug
            System.out.println("loading reviewByOrderList!");
            //
            reviewByOrderList = dao.loadReviewByOrder(orderID, userID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(reviewByOrderList == null || reviewByOrderList.isEmpty()) {
            System.out.println("reviewOrderList is null!");
        }
        view.displayProductsForReview(reviewByOrderList);
    }
}