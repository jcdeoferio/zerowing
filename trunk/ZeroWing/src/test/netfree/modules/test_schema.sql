CREATE TABLE students (
    pk serial primary key,
    studentname character varying(20),
    studentnumber integer
);

CREATE TABLE stupid_students (
    entity integer,
    attribute character varying(20),
    value text
);
