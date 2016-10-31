# etl-unifiedViews
Code and configuration related to running UnifiedViews as the ETL component.
# ASSUMPTIONS
Unified Views requires Backend (MariaDB) and Frontend (Apache Tomcat) servers. The commisioning procedure consists in installing MariaDB, configuring the DB schema and permissions, and installing the Frontend server. The DB configuration scripts can be found at https://github.com/tenforce/docker-unified-views. A ready-made Docker image for Unified Views backened that includes 

# STARTING MARIADB INSTANCE WITH OPTIONS
´´´
$ docker run --name my-mysql \
	-v /<EXPOSED_FILE_SYSTEM_DIRECTORY>/tmp \
        -p 3306:3306 \
        -e MYSQL_ROOT_PASSWORD=password \
        -e MYSQL_USER=unified_views_user \
        -e MYSQL_PASSWORD=unified_views_pwd \
        -e MYSQL_DATABASE=unified_views_db \
        -d mariadb
´´´

# CONFIGURING MARIADB SCHEMA AND PERMISSIONS 
A ready-made Docker image with configured schema and permissions can be found at https://hub.docker.com/r/attxproject/unifiedviewsbackend/.

Nevertheless, if you need to configure the Unified Views Backend's DB, you can still do it as follows: 
$ docker exec -it <MYSQL_CONTAINER_ID> bash
<MYSQL_CONTAINER_ID>$ mysql unified_views_db --user=root --password=password < /tmp/schema.sql
<MYSQL_CONTAINER_ID>$ mysql unified_views_db --user=root --password=password < /tmp/data-core.sql
<MYSQL_CONTAINER_ID>$ mysql unified_views_db --user=root --password=password < /tmp/permissions.sql


# CONNECTING TO MARIADB TO CHECK CONFS
$ docker exec -it <MYSQL_CONTAINER_ID> bash
<MYSQL_CONTAINER_ID>$ mysql --user=unified_views_user --password=unified_views_pwd;
<MYSQL_CONTAINER_ID>$ connect unified_views_db;

# RUNNING THE FRONTEND SERVER
$ docker run  \
    -p 8080:8080 --link my-mysql:mysql \
    -v /Users/joao/ATTX2016/UnifiedViews/:/unified-views/lib \
    -v /Users/joao/ATTX2016/UnifiedViews/:/dpus \
    -e UV_DATABASE_SQL_URL=jdbc:mysql://mysql:3306/unified_views_db?characterEncoding=utf8 \
    -e UV_DATABASE_SQL_USER=unified_views_user \
    -e UV_DATABASE_SQL_PASSWORD=unified_views_pwd \
    -e MASTER_API_USER=master \
    -e MASTER_API_PASSWORD=mysecretpassword \
    -d tenforce/unified-views
