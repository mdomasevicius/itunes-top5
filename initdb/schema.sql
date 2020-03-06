drop schema if exists public;

create schema if not exists itunes;

create role itunes_user login password 'itunes_pass';
alter role itunes_user set search_path to itunes;

alter default privileges in schema itunes
  grant all privileges on tables to itunes_user;

grant all on schema itunes to itunes_user with grant option;
