# --- !Ups

alter table members
drop column phone;

alter table members
drop column city;

# --- !Downs

alter table members
    add column phone varchar(20);

alter table members
    add column city varchar(100);