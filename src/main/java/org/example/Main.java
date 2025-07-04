package org.example;

import static spark.Spark.*;

import org.example.store.RedisStore;
import org.example.store.HazelcastStore;
import org.example.store.MongoStore;
import org.example.model.Student;

public class Main {
    public static void main(String[] args) {
        port(8080);

        RedisStore.init();
        HazelcastStore.init();
        MongoStore.init();

        // Redis endpoint
        get("/nosql-lab-rd/:param", (req, res) -> {
            res.type("text/html; charset=UTF-8");

            String param = req.params("param");
            String studentNo = extractStudentNo(param);
            if (studentNo == null) {
                res.status(400);
                return generateErrorHtml("Öğrenci numarası belirtilmedi veya format yanlış.");
            }

            Student student = RedisStore.get(studentNo);
            if (student == null) {
                res.status(404);
                return generateErrorHtml("Öğrenci bulunamadı (Redis).");
            }

            return generateStudentHtml(student, "Redis");
        });

        // Hazelcast endpoint
        get("/nosql-lab-hz/:param", (req, res) -> {
            res.type("text/html; charset=UTF-8");

            String param = req.params("param");
            String studentNo = extractStudentNo(param);
            if (studentNo == null) {
                res.status(400);
                return generateErrorHtml("Öğrenci numarası belirtilmedi veya format yanlış.");
            }

            Student student = HazelcastStore.get(studentNo);
            if (student == null) {
                res.status(404);
                return generateErrorHtml("Öğrenci bulunamadı (Hazelcast).");
            }

            return generateStudentHtml(student, "Hazelcast");
        });

        // MongoDB endpoint
        get("/nosql-lab-mon/:param", (req, res) -> {
            res.type("text/html; charset=UTF-8");

            String param = req.params("param");
            String studentNo = extractStudentNo(param);
            if (studentNo == null) {
                res.status(400);
                return generateErrorHtml("Öğrenci numarası belirtilmedi veya format yanlış.");
            }

            Student student = MongoStore.get(studentNo);
            if (student == null) {
                res.status(404);
                return generateErrorHtml("Öğrenci bulunamadı (MongoDB).");
            }

            return generateStudentHtml(student, "MongoDB");
        });
    }

    private static String extractStudentNo(String param) {
        if (param == null) return null;
        if (param.startsWith("student_no=") && param.length() > 11) {
            return param.substring(11);
        }
        return null;
    }

    private static String generateStudentHtml(Student student, String source) {
        return "<html><body>" +
                "<h1>Öğrenci Bilgileri (" + source + ")</h1>" +
                "<p><b>Numara:</b> " + student.getStudent_no() + "</p>" +
                "<p><b>İsim:</b> " + student.getName() + "</p>" +
                "<p><b>Bölüm:</b> " + student.getDepartment() + "</p>" +
                "</body></html>";
    }

    private static String generateErrorHtml(String message) {
        return "<html><body><h3>" + message + "</h3></body></html>";
    }
}