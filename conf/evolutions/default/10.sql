# --- !Ups

rename table blood_requests
to blood_transactions;

alter table blood_transactions
    change column units_required units int;

alter table blood_transactions
drop column hospital_name;

alter table blood_transactions
    add column transaction_type varchar(20);

alter table blood_transactions
    add column transaction_date datetime;

update blood_transactions
set transaction_type = 'OUTGOING'
where transaction_type is null;

update blood_transactions
set transaction_date = now()
where transaction_date is null;


# --- !Downs

alter table blood_transactions
drop column transaction_date;

alter table blood_transactions
drop column transaction_type;

alter table blood_transactions
    add column hospital_name varchar(255);

alter table blood_transactions
    change column units units_required int;

rename table blood_transactions
to blood_requests;