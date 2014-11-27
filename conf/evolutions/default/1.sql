# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table person (
  id                        varchar(255) not null,
  name                      varchar(255),
  constraint pk_person primary key (id))
;

create table workflow (
  uuid                      varchar(255) not null,
  status                    varchar(255),
  constraint pk_workflow primary key (uuid))
;

create sequence person_seq;

create sequence workflow_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists person;

drop table if exists workflow;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists person_seq;

drop sequence if exists workflow_seq;

