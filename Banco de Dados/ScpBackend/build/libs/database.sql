# create database if not exists scp;
# use scp;

create table if not exists temperature_sensor
(
    id          int      not null auto_increment,
    temperature double   not null,
    created_at  datetime not null default now(),
    primary key (id)
);

create or replace index idx_temperature_sensor_created_at on temperature_sensor (created_at);

create table if not exists humidity_sensor
(
    id         int      not null auto_increment,
    humidity   double   not null,
    created_at datetime not null default now(),
    primary key (id)
);

create or replace index idx_humidity_sensor_created_at on humidity_sensor (created_at);

create table if not exists soil_humidity_sensor
(
    id         int      not null auto_increment,
    raw        double   not null,
    percentage double   not null,
    created_at datetime not null default now(),
    primary key (id)
);

create or replace index idx_soil_humidity_sensor_created_at on soil_humidity_sensor (created_at);

create table if not exists light_sensor
(
    id         int      not null auto_increment,
    lux        double   not null,
    percentage double   not null,
    created_at datetime not null default now(),
    primary key (id)
);

create or replace index idx_light_sensor_created_at on light_sensor (created_at);

