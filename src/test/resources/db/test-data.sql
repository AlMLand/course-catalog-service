-- insert test data

insert into instructors(id, name) values (1, 'testInstructor1');
insert into instructors(id, name) values (2, 'testInstructor2');

insert into courses(id, name, category, instructor_id) values (1, 'testName1', 'testCategory1', 1);
insert into courses(id, name, category, instructor_id) values (2, 'testName2', 'testCategory2', 2);