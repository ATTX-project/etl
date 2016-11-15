FROM mariadb:latest

MAINTAINER João M. da Silva <joao.silva@helsinki.fi>

ADD schema.sql /docker-entrypoint-initdb.d/00-schema.sql
ADD data-core.sql /docker-entrypoint-initdb.d/01-data-core.sql
ADD data-permissions.sql /docker-entrypoint-initdb.d/02-data-permissions.sql
