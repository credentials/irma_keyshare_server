docker-compose rm --force --stop
docker-compose build --force-rm
docker-compose up -d --quiet-pull
