# --- !Ups

create table blood_requests (

                                id bigint auto_increment not null,

                                user_id bigint,

                                blood_group varchar(10),

                                units_required integer,

                                hospital_name varchar(255),

                                status varchar(50),

                                constraint pk_blood_requests
                                    primary key (id)

);

alter table blood_requests
    add constraint fk_blood_requests_user
        foreign key (user_id)
            references users(id);

# --- !Downs

drop table if exists blood_requests;