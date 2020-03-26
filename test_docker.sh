docker-compose rm --force --stop
docker-compose build --force-rm
docker-compose up -d --quiet-pull
docker exec -it $(docker ps | grep irma_keyshare_server | cut -f1 -d" ") /bin/bash

