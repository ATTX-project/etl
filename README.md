# etl-unifiedViews
Code and configuration related to running UnifiedViews as the ETL component.

# ASSUMPTIONS
Unified Views requires backend (MariaDB) and Frontend (Apache Tomcat) servers. The deployment procedure consists in creating the data volume container, starting up UnifiedViews Backend and the Frontend server. 

# RUNNING THE ATTX PROJECT'S CONTAINERISED IMPLEMENTATION OF UNIFIED VIEWS
Creating the data volume container:

``` 
docker create --name etldata attxproject/mariadb_persistance
```

Running the ATTX Project ready-made Docker image for Unified Views backend, that includes a preconfigured MariaDB (the SQL scripts can be found here: https://github.com/tenforce/docker-unified-views):

```
docker run --volumes-from etldata \
--name unifiedviewsbackend \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD=password \
-e MYSQL_USER=unified_views_user \
-e MYSQL_PASSWORD=unified_views_pwd \
-e MYSQL_DATABASE=unified_views_db \
-d attxproject/unifiedviewsbackend
```

Running the ATTX Project Unified Views frontend:


```
$ docker run --name unifiedviewsfrontend \
-p 8080:8080 --link unifiedviewsbackend:mysql \
-e UV_DATABASE_SQL_URL=jdbc:mysql://unifiedviewsbackend:3306/unified_views_db?characterEncoding=utf8 \
-e UV_DATABASE_SQL_USER=unified_views_user \
-e UV_DATABASE_SQL_PASSWORD=unified_views_pwd \
-e MASTER_API_USER=master \
-e MASTER_API_PASSWORD=mysecretpassword \
-d attxproject/unifiedviewsfrontend
```



# Verifying the containerised 
$ docker exec -it <MYSQL_CONTAINER_ID> bash
<MYSQL_CONTAINER_ID>$ mysql --user=unified_views_user --password=unified_views_pwd;
<MYSQL_CONTAINER_ID>$ connect unified_views_db;


