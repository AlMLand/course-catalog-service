-- insert test data

insert into instructors(first_name, last_name) values ('firstName1', 'lastName1');
insert into instructors(first_name, last_name) values ('firstName2', 'lastName2');

insert into course_categories(id, category, description) values ('00001234-0056-0078-0090-000000123456', 'D', 'description1');
insert into course_categories(id, category, description) values ('00000987-0065-0043-0021-000000098765', 'D', 'description2');
insert into course_categories(id, category, description) values ('00004444-0022-0022-0022-000000666666', 'M', 'description3');

insert into courses(id, name, instructor_firstname, instructor_lastname) values ('00001111-0011-0011-0011-000000111111', 'courseName1', 'firstName1', 'lastName1');
insert into courses(id, name, instructor_firstname, instructor_lastname) values ('00002222-0022-0022-0022-000000222222', 'courseName2', 'firstName2', 'lastName2');

insert into course_coursecategory(course_id, course_category_id) values ('00001111-0011-0011-0011-000000111111', '00001234-0056-0078-0090-000000123456');
insert into course_coursecategory(course_id, course_category_id) values ('00002222-0022-0022-0022-000000222222', '00000987-0065-0043-0021-000000098765');
insert into course_coursecategory(course_id, course_category_id) values ('00002222-0022-0022-0022-000000222222', '00004444-0022-0022-0022-000000666666');
