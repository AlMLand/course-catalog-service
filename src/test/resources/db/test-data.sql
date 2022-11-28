-- insert test data

insert into instructors(id, first_name) values (1, 'firstname1');
insert into instructors(id, first_name) values (2, 'firstname2');

insert into course_categories(id, category, description) values (1, 'DEVELOPMENT', 'testDescription1');
insert into course_categories(id, category, description) values (2, 'DEVELOPMENT', 'testDescription2');
insert into course_categories(id, category, description) values (3, 'MANAGEMENT', 'testDescription3');

insert into courses(id, name, instructor_id) values (1, 'testName1', 1);
insert into courses(id, name, instructor_id) values (2, 'testName2', 2);

insert into course_coursecategory(course_id, course_category_id) values (1, 1);
insert into course_coursecategory(course_id, course_category_id) values (2, 2);
insert into course_coursecategory(course_id, course_category_id) values (2, 3);
