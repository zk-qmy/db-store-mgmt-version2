package app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreenController implements ActionListener {
    private HomeScreen homeScreen;
    public HomeScreenController(HomeScreen homeScreen){
        this.homeScreen = homeScreen;

        homeScreen.getBtnLogin().addActionListener(this);
        homeScreen.getBtnCreateAccount().addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == homeScreen.getBtnCreateAccount()) {
            App.getInstance().getRegisterView().setVisible(true);
        } else if (e.getSource() == homeScreen.getBtnLogin()) {
            App.getInstance().getLoginView().setVisible(true);
        }
    }
}