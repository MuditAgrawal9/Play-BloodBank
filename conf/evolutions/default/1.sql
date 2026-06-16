-- !Ups

create table users (
                       id bigint auto_increment not null,
                       name varchar(255),
                       email varchar(255),
                       password varchar(255),
                       constraint pk_users primary key (id)
);

-- !Downs

drop table if exists users;