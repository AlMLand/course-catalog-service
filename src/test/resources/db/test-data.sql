-- insert test data

insert into instructors(first_name, last_name) values ('firstName1', 'lastName1');
insert into instructors(first_name, last_name) values ('firstName2', 'lastName2');

insert into course_categories(id, category, description) values (1, 'DEVELOPMENT', 'description1');
insert into course_categories(id, category, description) values (2, 'DEVELOPMENT', 'description2');
insert into course_categories(id, category, description) values (3, 'MANAGEMENT', 'description3');

insert into courses(id, name, instructor_firstname, instructor_lastname) values (1, 'courseName1', 'firstName1', 'lastName1');
insert into courses(id, name, instructor_firstname, instructor_lastname) values (2, 'courseName2', 'firstName2', 'lastName2');

insert into course_coursecategory(course_id, course_category_id) values (1, 1);
insert into course_coursecategory(course_id, course_category_id) values (2, 2);
insert into course_coursecategory(course_id, course_category_id) values (2, 3);
