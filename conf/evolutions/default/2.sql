# --- !Ups

create table donors (

                        id bigint auto_increment not null,

                        user_id bigint not null,

                        blood_group varchar(10),

                        age integer,

                        phone varchar(20),

                        city varchar(100),

                        constraint pk_donors primary key (id),

                        constraint uq_donors_user unique (user_id)

);

alter table donors
    add constraint fk_donors_user
        foreign key (user_id)
            references users(id);

# --- !Downs

drop table if exists donors;