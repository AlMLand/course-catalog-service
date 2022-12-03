-- insert test data

insert into instructors(first_name, last_name) values ('firstName1', 'lastName1');
insert into instructors(first_name, last_name) values ('firstName2', 'lastName2');

insert into course_categories(id, category, description) values ('00001234-0056-0078-0090-000000123456', 'DEVELOPMENT', 'description1');
insert into course_categories(id, category, description) values ('00000987-0065-0043-0021-000000098765', 'DEVELOPMENT', 'description2');
insert into course_categories(id, category, description) values ('00004444-0022-0022-0022-000000666666', 'MANAGEMENT', 'description3');

insert into courses(id, name, instructor_firstname, instructor_lastname) values (1, 'courseName1', 'firstName1', 'lastName1');
insert into courses(id, name, instructor_firstname, instructor_lastname) values (2, 'courseName2', 'firstName2', 'lastName2');

insert into course_coursecategory(course_id, course_category_id) values (1, '00001234-0056-0078-0090-000000123456');
insert into course_coursecategory(course_id, course_category_id) values (2, '00000987-0065-0043-0021-000000098765');
insert into course_coursecategory(course_id, course_category_id) values (2, '00004444-0022-0022-0022-000000666666');
