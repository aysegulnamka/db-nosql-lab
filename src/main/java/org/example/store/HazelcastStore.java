package org.example.store;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.example.model.Student;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HazelcastStore {
    private static IMap<String, Student> map;

    public static void init() {
        HazelcastInstance hz;
        try {
            hz = HazelcastClient.newHazelcastClient();
        } catch (Exception e) {
            System.err.println("Hazelcast client başlatılamadı!");
            e.printStackTrace();
            return;
        }

        map = hz.getMap("ogrenciler");

        if (map.isEmpty()) {
            System.out.println("Hazelcast boş, JSON'dan veriler yükleniyor...");
            try {
                InputStream inputStream = HazelcastStore.class
                        .getClassLoader()
                        .getResourceAsStream("student.json");

                if (inputStream == null) {
                    System.err.println("student.json dosyası bulunamadı!");
                    return;
                }

                Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Student>>() {}.getType();
                List<Student> students = gson.fromJson(reader, listType);

                for (Student s : students) {
                    map.put(s.getStudent_no(), s);
                }

                System.out.println("Hazelcast'e JSON verisi başarıyla yüklendi.");
            } catch (Exception e) {
                System.err.println("JSON verisi okunurken hata oluştu:");
                e.printStackTrace();
            }
        } else {
            System.out.println("Hazelcast daha önce yüklenmiş veriler içeriyor.");
        }
    }

    public static Student get(String student_no) {
        if (map == null) {
            System.err.println("Hazelcast map başlatılmamış.");
            return null;
        }
        return map.get(student_no);
    }
}
