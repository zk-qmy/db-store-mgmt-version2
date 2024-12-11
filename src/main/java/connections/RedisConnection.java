package connections;

import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class RedisConnection {
    private static RedisConnection instance;
    private UnifiedJedis jedis;

    public RedisConnection() {
        Properties propConfig = new Properties();
        try {
            propConfig.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String username = propConfig.getProperty("redis.username");
        String password = propConfig.getProperty("redis.password");
        String host = propConfig.getProperty("redis.host");
        String portString = propConfig.getProperty("redis.port");
        int port = Integer.parseInt(portString);
        try {
            JedisClientConfig config = DefaultJedisClientConfig.builder()
                    .user(username)
                    .password(password)
                    .build();

            this.jedis = new UnifiedJedis(
                    new HostAndPort(host, port),config
            );
            // Test connection
            String pingResponse = jedis.ping();
            System.out.println("Connection successful! Server response: " + pingResponse);
        } catch (JedisException e) {
            e.printStackTrace();
        }

    }
    public static synchronized RedisConnection getInstance() {
        if (instance == null) {
            instance = new RedisConnection();
        }
        return instance;
    }
    public UnifiedJedis getJedis(){
        return jedis;
    }
    // Close the Jedis connection
    public void close() {
        if (jedis != null) {
            jedis.close();
            System.out.println("Redis connection closed.");
        }
    }
    /*

    public static void main(String[] args) {
        RedisConnection redisConnection = RedisConnection.getInstance();
        UnifiedJedis jedis = redisConnection.getJedis();

        try {
            // List all keys in Redis
            Set<String> keys = jedis.keys("*");

            // Print key-value pairs
            for (String key : keys) {
                String value = jedis.get(key);  // Get the value for the key
                System.out.println("Key: " + key + ", Value: " + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure the connection is closed
            if (jedis != null) {
                jedis.close();
            }
        }
    }*/
}
