drop table if exists client;
create table client (id identity primary key auto_increment,
                     name varchar2 not null);
insert into client (id, name)
values (0, 'Ivan Ivanov'), (1, 'Petr Petrov'), (2, 'Petr Petrov'), (3, 'Petr Petrov');

drop table if exists account;
create table account (id identity primary key auto_increment,
                      nubmer varchar2 not null,
                      total_balance number(10,2) default 0 not null,
                      res_balance number(10,2)  default 0 not null,
                      client_id int not null);
alter table account
    add foreign key (client_id)
        references client(id) on delete cascade;

insert into account (id, nubmer, total_balance, res_balance, client_id)values
(0, 'Test7777', 555.00, 0.00, 0),
(1, 'Test99999', 333.00, 0.00, 1),
(2, 'Test99999', 444.00, 0.00, 2),
(3, 'Test99999', 3);