package org.example.store;

import redis.clients.jedis.Jedis;
import com.google.gson.Gson;
import org.example.model.Student;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class RedisStore {
    private static JedisPool jedisPool;
    private static final Gson gson = new Gson();

    public static void init() {
        jedisPool = new JedisPool(new JedisPoolConfig(), "localhost", 6379);
        try (Jedis jedis = jedisPool.getResource()) {
            System.out.println("Redis bağlandı: " + jedis.ping());

            if (jedis.dbSize() == 0) {
                System.out.println("student.json dosyasından veri yükleniyor...");
                try (Reader reader = new FileReader("src/main/resources/student.json")) {
                    Student[] students = gson.fromJson(reader, Student[].class);

                    for (Student s : students) {
                        String json = gson.toJson(s);
                        jedis.set(s.getStudent_no(), json);
                    }
                    System.out.println("Veri yükleme tamamlandı.");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("JSON dosyası okunurken hata oluştu.");
                }
            }
        }
    }

    public static Student get(String student_no) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(student_no);
            if (json == null) return null;
            return gson.fromJson(json, Student.class);
        }
    }
}