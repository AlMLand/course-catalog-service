--liquibase formatted sql

--changeset amorland:1

create table course_categories (id varchar(36) not null, category varchar(255) not null, description varchar(255), primary key (id));

create table course_categories_view (id varchar(36) not null, category varchar(255) not null, description varchar(255), primary key (id));

create table course_coursecategory (course_id varchar(36) not null, course_category_id varchar(36) not null);

create table courses (id varchar(36) not null, name varchar(255) not null, instructor_firstname varchar(255) not null, instructor_lastname varchar(255) not null, primary key (id));

create table courses_view (id varchar(36) not null, instructor_firstname varchar(255) not null, instructor_lastname varchar(255) not null, name varchar(255) not null, primary key (id));

create table instructors (first_name varchar(255) not null, last_name varchar(255) not null, primary key (first_name, last_name));

create table instructors_view (first_name varchar(255) not null, last_name varchar(255) not null, primary key (first_name, last_name));

alter table course_coursecategory add constraint FK4pd50hjko9ufi5qhbr2y20ard foreign key (course_category_id) references course_categories;

alter table course_coursecategory add constraint FKsbn284dlcwf8hgd8e06lsi9l6 foreign key (course_id) references courses;

alter table courses add constraint FK1o2xajndm090j1w7tvrt37jas foreign key (instructor_firstname, instructor_lastname) references instructors;