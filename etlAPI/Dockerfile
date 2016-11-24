FROM  python:3.4.5-alpine

#Dockerfile maintainer
MAINTAINER Stefan Negru <stefan.negru@helsinki.fi>

ENV ETL API

RUN apk add --update --repository http://dl-cdn.alpinelinux.org/alpine/edge/community/\
    & apk add --no-cache build-base

EXPOSE 4301

WORKDIR /app
COPY server /app
RUN pip install -r requirements.txt

CMD ["python", "app.py"]
