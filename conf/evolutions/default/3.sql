# --- !Ups

create table blood_inventory (

                                 id bigint auto_increment not null,

                                 blood_group varchar(10),

                                 units_available integer,

                                 constraint pk_blood_inventory
                                     primary key (id)

);

# --- !Downs

drop table if exists blood_inventory;