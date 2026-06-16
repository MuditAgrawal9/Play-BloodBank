# --- !Ups

rename table donors to members;

alter table users
add column role varchar(20);

# --- !Downs

rename table members to donors;

alter table users
drop column role;