DROP DATABASE IF EXISTS Company;
CREATE DATABASE IF NOT EXISTS Company;
use Company;
DROP TABLE IF EXISTS employee;
CREATE TABLE IF NOT EXISTS employee(
                               id         BIGINT PRIMARY KEY AUTO_INCREMENT,
                               name       varchar(20),
                               department varchar(20),
                               duty       varchar(20),
                               status    BOOLEAN,          -- he/she is on company
                               position   varchar(20)
)AUTO_INCREMENT=20260001 ;
INSERT INTO employee(name,department,duty,position)VALUES
        ('CONAN DOYLE','SALES DEPARTMENT','SELL PRODUCT','MANAGER');
SELECT*FROM employee;

