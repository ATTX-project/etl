# etl-unifiedViews
Code and configuration related to running UnifiedViews as the ETL component.

# ASSUMPTIONS
Unified Views requires backend (MariaDB) and Frontend (Apache Tomcat) servers. The deployment procedure consists in creating the data volume container, starting up UnifiedViews Backend and the Frontend server. 

# RUNNING THE ATTX PROJECT'S CONTAINERISED IMPLEMENTATION OF UNIFIED VIEWS
Creating the data volume container:

``` 
docker create --name etldata attxproject/mariadb_persistance
```

A ready-made Docker image for Unified Views backend that includes a preconfigured MariaDB (the SQL scripts can be found here: https://github.com/tenforce/docker-unified-views.) 

The instance for UnifiedViews backend use can be found at Run command, note that you need to have the "etldata" container in place beforehand (https://hub.docker.com/r/attxproject/mariadb_persistance/):

```

```


```
$ docker run --volumes-from etldata \
--name unifiedviewsbackend \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD=password \
-e MYSQL_USER=unified_views_user \
-e MYSQL_PASSWORD=unified_views_pwd \
-e MYSQL_DATABASE=unified_views_db \
-d attxproject/unifiedviewsbackend
```


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
