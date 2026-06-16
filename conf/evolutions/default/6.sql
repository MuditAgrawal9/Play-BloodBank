# --- !Ups

alter table users
    add column phone varchar(20);

alter table users
    add column city varchar(100);

alter table users
    add column address varchar(255);

# --- !Downs

alter table users
drop column phone;

alter table users
drop column city;

alter table users
drop column address;