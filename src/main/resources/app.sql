use moviever_db;

drop table users;
create table users(
    id int primary key,

);


drop table reviews;
create table reviews(
    id int primary key,
    user_id int references users(id),
    movie_id int references movies(id),
);