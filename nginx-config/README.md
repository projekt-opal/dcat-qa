# Nginx Config

Config files for the deployment of a nginx reverse proxy on the openbot vm.
Including Dockerfile to build custom nginx container.

## Build

Build yourself with (run in `nginx-config` folder)
```
docker build  -t nginx-openbot .
```
 or pull from gitlab container registry with 
```
docker login hub.cs.upb.de
docker pull hub.cs.upb.de/martenls/bachelor-thesis-code/nginx-openbot
```

## Run

On openbot vm with
```
docker run --restart unless-stoped --name nginx -p 80:80 -p 443:443 -v /etc/nginx/ssl:/etc/nginx/ssl nginx-openbot
```
or use provided `docker-compose.yml`.