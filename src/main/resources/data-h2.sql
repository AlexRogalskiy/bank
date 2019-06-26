drop table if exists client;
create table client (id identity primary key auto_increment,
                     name varchar2 not null);
insert into client (id, name)
values (0, 'Ivan Ivanov'), (1, 'Petr Petrov'), (2, 'Petr Petrov'), (3, 'Petr Petrov');
