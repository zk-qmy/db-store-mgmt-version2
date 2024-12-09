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
    public boolean storeReview(){
        Map<ObjectId, String> collectedReviews = view.collectReview();
        if (collectedReviews == null || collectedReviews.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please enter review before submitting!");
            return false;
        }

        if (dao.updateReviews(collectedReviews)) {
            JOptionPane.showMessageDialog(null, "Reviews successfully updated!");
            // reintialise the map
            view.reviewTextAreas.clear();
            return true;
        }
        return false;
    }

    public void loadReviewByOrderList(){
        int orderID = view.currentOrderID;
        int userID = Session.getInstance().getCurrentUser().getUserID();
        System.out.println("orderID: " + orderID);

        List<Document> reviewByOrderList = null;
        try{
            // debug
            System.out.println("loading reviewByOrderList!");
            reviewByOrderList = dao.loadReviewByOrder(orderID, userID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(reviewByOrderList == null || reviewByOrderList.isEmpty()) {
            System.out.println("reviewOrderList is null!");
        }
        //debug
        for (Document doc : reviewByOrderList) {
            System.out.println(doc.toJson());
            System.out.println("printed reviewByOrderList!");

        }
        view.displayProductsForReview(reviewByOrderList);
    }
}