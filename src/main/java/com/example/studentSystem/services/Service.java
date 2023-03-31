package com.example.studentSystem.services;

import com.example.studentSystem.models.Student;

import java.sql.*;
import java.util.*;

@org.springframework.stereotype.Service
public class Service {
    //Конекшены к бдшкам
    Connection connToStudents;
    Connection connToMarks;
    Connection connToSubjects;

    //Ссылка на базу данных
    String url_students = "jdbc:sqlite:student.db";
    String url_marks = "jdbc:sqlite:mark.db";
    String url_subjects = "jdbc:sqlite:subject.db";

    //Строки для SQL-запросов
    String sqlInsert = "INSERT INTO student_table(id, name, surname, age, city, direction) VALUES(?,?,?,?,?,?)";
    String sqlDelete = "DELETE FROM student_table WHERE id = ";
    String sqlCreate;

    //Список предметов
    Map<String, String[]> subjects = new HashMap<>() {{
        try {
            //Лист предметами для каждого направления
            List<String> subjects;

            connToSubjects = DriverManager.getConnection(url_subjects);
            Statement stmtDirection = connToSubjects.createStatement();
            //Получение названий направлений
            ResultSet rsDirection = stmtDirection.executeQuery("SELECT * FROM sqlite_master;");

            //Перебор направленией
            while (rsDirection.next()) {
                subjects = new ArrayList<>();
                String name = rsDirection.getString("name");

                //Получение предметов направления
                Statement stmtTempDirection = connToSubjects.createStatement();
                ResultSet rsSubjects = stmtTempDirection.executeQuery("SELECT * FROM " + name);

                //Перебор предметов направления
                while (rsSubjects.next()) {
                    subjects.add(rsSubjects.getString("name"));
                }

                put(name, subjects.toArray(new String[0]));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }};

    int ID = 0;

    private final List<Student> studentList = new ArrayList<>();

    {
        try {
            ID = 1;
            connToMarks = DriverManager.getConnection(url_marks);
            connToStudents = DriverManager.getConnection(url_students);

            Statement stmtStudent = connToStudents.createStatement();
            ResultSet rsStudents = stmtStudent.executeQuery("SELECT * FROM student_table");

            while (rsStudents.next()) {
                Statement stmtMark = connToMarks.createStatement();
                ResultSet rsMarks = stmtMark.executeQuery("SELECT * FROM student" + ID);

                List<String> dateList = new ArrayList<>();
                List<String> subjectList = new ArrayList<>();
                List<Integer> markList = new ArrayList<>();

                while (rsMarks.next()) {
                    dateList.add(rsMarks.getString("Date"));
                    subjectList.add(rsMarks.getString("Subject"));
                    markList.add(rsMarks.getInt("Mark"));
                }

                ID++;

                studentList.add(new Student(rsStudents.getInt("id"), rsStudents.getString("name"), rsStudents.getString("surname"),
                        rsStudents.getInt("age"), rsStudents.getString("city"), rsStudents.getString("direction"), dateList, subjectList, markList));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Student> listStudents() {
        return studentList;
    }

    public Set<String> listDirections() {
        return subjects.keySet();
    }

    public Student getStudentById(int id) {
        for (Student student : studentList) {
            if (student.getId() == id) return student;
        }

        return null;
    }

    public void saveStudent(Student student) {
        //Сохранение студента в БД
        try {
            studentList.add(student);

            //Запись данных о новом студенте в БД
            PreparedStatement pstmt = connToStudents.prepareStatement(sqlInsert);

            //Заполнение запроса данными
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getSurname());
            pstmt.setInt(4, student.getAge());
            pstmt.setString(5, student.getCity());
            pstmt.setString(6, student.getEducationalDirection());

            //Исполнение запроса
            pstmt.executeUpdate();
            pstmt.close();

            sqlCreate = "CREATE TABLE IF NOT EXISTS student" + student.getId() + " (Date TEXT, Subject TEXT, Mark INTEGER);";

            Statement stmt = connToMarks.createStatement();
            stmt.execute(sqlCreate);
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStudent(int id) {
        //Удаление из листа
        studentList.removeIf(student -> student.getId() == id);
        ID--;

        //Удаление студента по id из БД
        try {
            //Из всех студентов
            PreparedStatement pstmt = connToStudents.prepareStatement(sqlDelete + id);
            pstmt.executeUpdate();
            pstmt.close();

            //Персональная таблица оценок
            Statement stmt = connToMarks.createStatement();
            stmt.execute("DROP TABLE IF EXISTS student" + id + ";");
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertMark(String date, String subject, int mark, int id) {
        try {
            //Изменение студента
            Student student = getStudentById(id);
            student.markList.add(mark);
            student.dateList.add(date);
            student.subjectList.add(subject);

            studentList.set(studentList.indexOf(student), student);

            //Инсёрт в базу
            String sqlInsert = "INSERT INTO student" + id + " VALUES(?,?,?)";
            PreparedStatement stmt = connToMarks.prepareStatement(sqlInsert);
            stmt.setString(1, date);
            stmt.setString(2, subject);
            stmt.setInt(3, mark);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getSubjectsByDirection(String educationDirection) {
        return subjects.get(educationDirection);
    }

}
