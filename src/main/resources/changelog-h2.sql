drop table if exists client;
create table client
(
    id   identity primary key auto_increment,
    name varchar2 not null
);
insert into client (id, name)
values (0, 'Ivan Ivanov'),
       (1, 'Petr Petrov'),
       (2, 'Petr Petrov'),
       (3, 'Petr Petrov');

drop table if exists account;
create table account
(
    id            identity primary key auto_increment,
    number        varchar2                not null,
    balance number(10, 2) default 0 not null,
    client_id     int                     not null
);
alter table account
    add foreign key (client_id)
        references client (id) on delete cascade;

insert into account (id, number, balance, client_id)
values (0, 'Test7777', 555.00, 0),
       (1, 'Test99999', 100.00, 1),
       (2, 'Test33333', 444.00, 2),
       (3, 'Test11111', 555.00, 3);

drop table if exists transfer;
create table transfer
(
    id              identity primary key auto_increment,
    creation_date   timestamp default now() not null,
    to_account_id   int                     not null,
    from_account_id int                     not null,
    amount          number(10, 2)           not null,
    client_id       int                     not null
);

alter table transfer
    add foreign key (from_account_id)
        references account (id) on delete cascade;

alter table transfer
    add foreign key (client_id)
        references client (id) on delete cascade;

alter table transfer
    add foreign key (to_account_id)
        references account (id) on delete cascade;

insert into transfer (id, creation_date, amount, to_account_id, from_account_id, client_id)
values (0, now(), 55.00, 1, 0, 1),
       (1, now(), 30.00, 0, 1, 1),
       (2, now(), 11.15, 0, 1, 1);