# uv-add-dpus

A docker image to add dpus to your unified-views instance using the master api. 

Usage: 
Add all official dpus using `docker run --rm --link uv-frontend:frontend tenforce/unified-views-add-dpus` 
Add your own dpus using `docker run --rm --link unifiedviews_frontend_1:frontend -v /path/to/your/dpus:/dpus tenforce/unified-views-add-dpus`

Provide your master user and password using environment
```
#MASTER_USER=master
#MASTER_PASSWORD=commander
```
