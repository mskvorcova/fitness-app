FROM ubuntu:latest
LABEL authors="mariaskvorcova"

FROM alpine:3.20
RUN apk add --no-cache curl tzdata
ENV TZ=Europe/Riga

COPY run.sh /run.sh
RUN chmod +x /run.sh

RUN echo "0 0 * * * /run.sh >> /var/log/cron.log 2>&1" > /etc/crontabs/root

CMD ["crond","-f","-l","2"]
