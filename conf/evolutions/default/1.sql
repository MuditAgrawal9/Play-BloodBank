# --- !Ups

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT NOT NULL,
                       name VARCHAR(255),
                       email VARCHAR(255),
                       password VARCHAR(255),
                       role VARCHAR(20),
                       phone VARCHAR(20),
                       city VARCHAR(100),
                       address VARCHAR(255),
                       pincode VARCHAR(10),

                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE donors (
                        id BIGINT AUTO_INCREMENT NOT NULL,
                        user_id BIGINT NOT NULL,
                        blood_group VARCHAR(10),
                        age INT,

                        CONSTRAINT pk_donors PRIMARY KEY (id),
                        CONSTRAINT uq_donors_user UNIQUE (user_id)
);

ALTER TABLE donors
    ADD CONSTRAINT fk_donors_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);

CREATE TABLE blood_inventory (
                                 id BIGINT AUTO_INCREMENT NOT NULL,
                                 blood_group VARCHAR(10),
                                 units_available INT,

                                 CONSTRAINT pk_blood_inventory PRIMARY KEY (id)
);

CREATE TABLE blood_transactions (
                                    id BIGINT AUTO_INCREMENT NOT NULL,
                                    user_id BIGINT,
                                    blood_group VARCHAR(10),
                                    units INT,
                                    transaction_type VARCHAR(20),
                                    status VARCHAR(50),
                                    transaction_date DATETIME,

                                    CONSTRAINT pk_blood_transactions PRIMARY KEY (id)
);

ALTER TABLE blood_transactions
    ADD CONSTRAINT fk_blood_transactions_user
        FOREIGN KEY (user_id)
            REFERENCES users(id);


# --- !Downs

DROP TABLE IF EXISTS blood_transactions;

DROP TABLE IF EXISTS blood_inventory;

DROP TABLE IF EXISTS donors;

DROP TABLE IF EXISTS users;