package org.example.store;

import com.google.gson.Gson;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.example.model.Student;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class MongoStore {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;
    private static final Gson gson = new Gson();

    public static void init() {
        // MongoDB bağlantısı (localhost, 27017)
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("nosql_lab");
        collection = database.getCollection("students");

        // Eğer koleksiyon boşsa JSON dosyasından yükle
        if (collection.countDocuments() == 0) {
            System.out.println("student.json dosyasından veri yükleniyor (MongoDB)...");

            try (Reader reader = new FileReader("src/main/resources/student.json")) {
                Student[] students = gson.fromJson(reader, Student[].class);

                for (Student s : students) {
                    String json = gson.toJson(s);
                    Document doc = Document.parse(json);
                    collection.insertOne(doc);
                }

                System.out.println("Veri yükleme tamamlandı (MongoDB).");

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("JSON dosyası okunurken hata oluştu (MongoDB).");
            }
        } else {
            System.out.println("MongoDB koleksiyonu zaten dolu.");
        }
    }

    public static Student get(String student_no) {
        Document doc = collection.find(Filters.eq("student_no", student_no)).first();

        if (doc == null) return null;

        String json = doc.toJson();
        return gson.fromJson(json, Student.class);
    }
}
