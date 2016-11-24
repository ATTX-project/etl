FROM  python:3.4.3-onbuild

#Dockerfile maintainer
MAINTAINER Stefan Negru <stefan.negru@helsinki.fi>

ENV ETL API

COPY server /
# WORKDIR /server

EXPOSE 4301

CMD [ "python", "server/app.py" ]
