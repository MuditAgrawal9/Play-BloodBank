# --- !Ups

ALTER TABLE users
    ADD COLUMN pincode VARCHAR(10);

# --- !Downs

ALTER TABLE users
DROP COLUMN pincode;