
CREATE TABLE PERSONS (
    ID VARCHAR(64) PRIMARY KEY,
    NAME VARCHAR(64) NOT NULL,
    firstNAME VARCHAR(64) NOT NULL,
    GENDER VARCHAR(50) NOT NULL CHECK (GENDER IN ('MALE','FEMALE')),
    BDATE TIMESTAMP
);

CREATE TABLE TEACHERS (
    FK_PERSON VARCHAR(64) PRIMARY KEY,
    HDATE TIMESTAMP,
    SALARY INTEGER NOT NULL CHECK (SALARY > 0),
    FOREIGN KEY (FK_PERSON)
        REFERENCES PERSONS (ID)
);

CREATE TABLE CLASSES(
    ID VARCHAR(64) PRIMARY KEY,
    NAME VARCHAR(64) NOT NULL,
    FK_TEACHER VARCHAR(64) NOT NULL,
    FOREIGN KEY (FK_TEACHER)
        REFERENCES TEACHERS (FK_PERSON)
);

CREATE TABLE STUDENTS (
    FK_PERSON VARCHAR(64) PRIMARY KEY,
    FK_CLASS VARCHAR(64) NOT NULL,
    GRADE INTEGER NOT NULL CHECK (GRADE IN (1,2,3,4,5)),
    FOREIGN KEY (FK_PERSON)
        REFERENCES PERSONS (ID),
    FOREIGN KEY (FK_CLASS)
        REFERENCES CLASSES (ID)
);

CREATE TABLE COURSES(
    ID VARCHAR(64) PRIMARY KEY,
    HACTIVE INTEGER NOT NULL,
    NAME VARCHAR(64) NOT NULL,
    FK_TEACHER VARCHAR(64) NOT NULL,
    FOREIGN KEY (FK_TEACHER)
        REFERENCES TEACHERS (FK_PERSON)
);

CREATE TABLE STUDENT_COURSES(
    FK_STUDENT VARCHAR(64),
    FK_COURSE VARCHAR(64),
    PRIMARY KEY (FK_STUDENT,FK_COURSE),
    FOREIGN KEY (FK_STUDENT)
        REFERENCES STUDENTS (FK_PERSON),
    FOREIGN KEY (FK_COURSE)
        REFERENCES COURSES (ID)
);
