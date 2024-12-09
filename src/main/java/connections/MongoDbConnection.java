package connections;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MongoDbConnection {
    private static MongoDbConnection instance;
    private MongoClient mongoClient;

    private MongoDbConnection() {
        Properties config = new Properties();
        try {
            config.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String username = config.getProperty("mongo.username");
        String password = config.getProperty("mongo.password");

        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8);
        String connectionString = "mongodb+srv://"+encodedUsername+ ":"+encodedPassword+
                                  "@cluster0.1bg9u.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Create a new client and connect to the server
        mongoClient = MongoClients.create(settings);
        try {
            // Send a ping to confirm a successful connection
            MongoDatabase database = mongoClient.getDatabase("admin");
            database.runCommand(new Document("ping", 1));
            System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static synchronized MongoDbConnection getInstance() {
        if (instance == null) {
            instance = new MongoDbConnection();
        }
        return instance;
    }

    public MongoDatabase getMongoDatabase(String dbName){
        if (mongoClient == null) {
            throw new IllegalStateException("MongoClient is not initialized!");
        }
        System.out.println("Connected to database: " + dbName);
        return mongoClient.getDatabase(dbName);
    }

    public void closeMongoConn(){
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed!");
        }
    }
    /*public static void main(String[] args) {
        MongoDbConnection connection = MongoDbConnection.getInstance();
        // Example usage
        MongoDatabase database = connection.getMongoDatabase("db-project2");
        //System.out.println("Connected to database: " + database.getName());
        MongoCollection<Document> collection = database.getCollection("reviews");
        // Fetch all documents from the collection
        List<Document> documents = collection.find().into(new ArrayList<>());

        // Print all documents
        for (Document doc : documents) {
            System.out.println(doc.toJson());
        }
        connection.closeMongoConn();
    }*/
}