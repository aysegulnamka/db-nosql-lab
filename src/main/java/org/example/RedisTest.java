package org.example;
import redis.clients.jedis.Jedis;

public class RedisTest {
    public static void main(String[] args) {
        try (Jedis jedis = new Jedis("127.0.0.1", 6379)) {
            System.out.println("Redis'e bağlanılıyor...");
            String pong = jedis.ping();
            System.out.println("Ping sonucu: " + pong);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
