# Student System
Login page:

![image](https://github.com/Aleksandeeer/studentSystem/assets/90092786/4d81c469-9363-4a79-b5c8-90b865d984c7)

Main page:

![image](https://github.com/Aleksandeeer/studentSystem/assets/90092786/2d8cf3a0-d5ab-47bd-a7f0-f39951d31c13)

Adding student:

![image](https://github.com/Aleksandeeer/studentSystem/assets/90092786/15e5b22f-6ea7-49f3-9344-83eb69230b3b)

Page of student (opening after clicking ID of student on Main page):

![image](https://github.com/Aleksandeeer/studentSystem/assets/90092786/c9aa9010-32a0-424b-8d7b-464f9a19a2e0)

TABLES FOR PROJECT (PostgreSql)
CREATE TABLE IF NOT EXISTS student_table (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255),
    age INTEGER,
    city VARCHAR(255),
    direction VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS mark_table (
    id SERIAL PRIMARY KEY,
    student_id INTEGER,
    date DATE,
    subject VARCHAR(255),
    mark INTEGER,
    FOREIGN KEY (student_id) REFERENCES student_table(id)
);

CREATE TABLE IF NOT EXISTS subject_table (
    id SERIAL PRIMARY KEY,
    direction_name VARCHAR(255),
    subject_name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Users (
	UserID INT PRIMARY KEY,
	UserLogin VARCHAR(30),
	UserPasswordSHA256 VARCHAR(64),
	UserRole VARCHAR(5)
);
