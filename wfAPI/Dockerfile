FROM  python:3.4.5-alpine

MAINTAINER Stefan Negru <stefan.negru@helsinki.fi>

RUN apk add --update --repository http://dl-cdn.alpinelinux.org/alpine/edge/community/\
    && apk add --no-cache build-base

EXPOSE 4301

WORKDIR /app
COPY server /app

RUN pip install --no-cache-dir --upgrade pip \
    && pip install --no-cache-dir -r requirements.txt

CMD gunicorn -w 4 -b 0.0.0.0:4301 app:app
