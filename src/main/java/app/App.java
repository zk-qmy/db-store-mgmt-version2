package app;

import admin.dashboard.DashBoardController;
import admin.dashboard.DashBoardView;
import admin.ordersmgmt.AdOrdersController;
import admin.ordersmgmt.AdOrdersView;
import admin.productsmgmt.AdProductsController;
import admin.productsmgmt.AdProductsView;
import admin.usersmgmt.AdUserController;
import admin.usersmgmt.AdUserView;
import com.mongodb.client.MongoDatabase;
import connections.MongoDbConnection;
import connections.RedisConnection;
import customer.browser.BrowserController;
import customer.browser.BrowserView;
import customer.cart.CartControllerMG;
import customer.cart.CartView;
import customer.orderhistory.OrderHisController;
import customer.orderhistory.OrderHisView;
import customer.reviews.ReviewDAO;
import orders.OrdersCollection;
import products.ProductsDAO;
import register.Session;
import register.createaccount.RegisterController;
import register.createaccount.RegisterView;
import register.users.UsersDAO;
import register.login.LoginController;
import register.login.LoginView;
import customer.reviews.ReviewView;
import customer.reviews.ReviewController;

public class App {
    private static App instance;
    // Main components
    private final UsersDAO usersDAO;
    //private final OrdersDAO ordersDAO;
    //private final OrderDetailsDAO orderDetailsDAO;
    private final CartView cartView;
    private final ProductsDAO productsDAO;
    private final ReviewDAO reviewDAO;
    private final OrdersCollection ordersCollection;

    private final BrowserView browserView;
    private final OrderHisView orderHisView;
    private final HomeScreen homeScreen;
    private final RegisterView registerView;
    private final LoginView loginView;
    private final ReviewView reviewView;

    private final DashBoardView dashBoardView;
    private final AdProductsView adProductsView;
    private final AdUserView adUserView;
    private final AdOrdersView adOrdersView;

    // Controllers
    private final HomeScreenController homeScreenController;
    private final RegisterController registerController;
    private final LoginController loginController;
    //private final OrderHisController orderHisController;
    // => private final CartController cartController;
    private final BrowserController browserController;
    //private final ReviewController reviewController;
    private final CartControllerMG cartControllerMG;

    private final AdProductsController adProductsController;
    //=> private final AdUserController adUserController;
    //=>private final AdOrdersController adOrdersController;
    private final DashBoardController dashBoardController;


    private static MongoDatabase database = MongoDbConnection.getInstance().getMongoDatabase("db-project2");
    private static RedisConnection redisConnection = RedisConnection.getInstance();


    // Constructor with Dependency Injection
    private App(UsersDAO usersDAO,
                ProductsDAO productsDAO,
                ReviewDAO reviewDAO,
                OrdersCollection ordersCollection) {
        this.usersDAO = usersDAO;
        //this.ordersDAO = ordersDAO;
        //this.orderDetailsDAO = orderDetailsDAO;
        this.productsDAO = productsDAO;
        this.reviewDAO = reviewDAO;
        this.ordersCollection = ordersCollection;

        this.homeScreen = new HomeScreen();
        this.registerView = new RegisterView();
        this.loginView = new LoginView();
        this.adProductsView = new AdProductsView();
        this.orderHisView = new OrderHisView();
        this.cartView = new CartView();
        this.browserView = new BrowserView();
        this.adUserView = new AdUserView();
        this.adOrdersView = new AdOrdersView();
        this.dashBoardView = new DashBoardView();

        this.reviewView = new ReviewView();

        // Initialize controllers
        this.homeScreenController = new HomeScreenController(homeScreen);
        this.registerController = new RegisterController(registerView, usersDAO, homeScreen);
        this.loginController = new LoginController(loginView, usersDAO, homeScreen);
        this.browserController = new BrowserController(browserView, productsDAO);

        //=>this.cartController = new CartController(cartView, orderDetailsDAO, productsDAO);
        this.cartControllerMG = new CartControllerMG(cartView, ordersCollection, productsDAO, reviewDAO);

        this.adProductsController = new AdProductsController(adProductsView, productsDAO);
        //=>this.adUserController = new AdUserController(adUserView, usersDAO, ordersCollection);
        //=>this.adOrdersController = new AdOrdersController(adOrdersView, ordersCollection, reviewDAO);
        this.dashBoardController = new DashBoardController(dashBoardView, productsDAO, ordersCollection, usersDAO);

    }

    public static App getInstance() {
        if (instance == null) {
            // Inject dependencies when creating the App instance
            instance = new App(new UsersDAO(redisConnection),
                    new ProductsDAO(),
                    new ReviewDAO(database),
                    new OrdersCollection(database, redisConnection));
        }
        return instance;
    }

    public OrderHisController getOrderHisController(){
        if (Session.getInstance().getCurrentUser() != null) {
            return new OrderHisController(orderHisView, ordersCollection);
            //=>return new OrderHisController(orderHisView, ordersDAO);
        } else {
            throw new IllegalStateException("No user login yet!");
        }
    }
    public AdOrdersController getAdOrdersController(){
        if(Session.getInstance().getCurrentUser()!=null) {
            return new AdOrdersController(adOrdersView, ordersCollection, reviewDAO);
        } else{
            throw new IllegalStateException("No user login yet!");
        }
    }
    public AdUserController getAdUserController(){
        if(Session.getInstance().getCurrentUser()!= null) {
            return new AdUserController(adUserView, usersDAO, ordersCollection);
        }else{
            throw new IllegalStateException("No user login yet!");
        }
    }
    public ReviewController getReviewController(){
        if (Session.getInstance().getCurrentUser() != null) {
            return new ReviewController(reviewView, reviewDAO);
        } else {
            throw new IllegalStateException("No user login yet!");
        }
    }

    public OrderHisView getOrderHisView() {return orderHisView;}
    public AdProductsView getAdProductsView() {return adProductsView;}
    public CartView getCartView() {return cartView;}
    public BrowserView getBrowserView() {
        return browserView;
    }
    public RegisterView getRegisterView() {return registerView;}
    public LoginView getLoginView() {return loginView;}
    public HomeScreen getHomeScreen() {
        return homeScreen;
    }
    public ReviewView getReviewView() {return reviewView;}
    public DashBoardView getDashBoardView(){return dashBoardView;}
    public AdUserView getAdUserView() {return adUserView;}
    public AdOrdersView getAdOrdersView(){return adOrdersView;}

    // Main class
    public static void main(String[] args) {
        App.getInstance().getHomeScreen().setVisible(true);
        // developing view
        //App.getInstance().getReviewView().setVisible(true);
    }
}
