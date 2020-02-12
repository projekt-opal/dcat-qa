FROM nginx

WORKDIR /etc/nginx/

COPY nginx.conf .

COPY mime.types .

COPY nginxconfig.io ./nginxconfig.io

COPY sites-available ./sites-available

RUN mkdir sites-enabled

RUN ln sites-available/openbot.cs.uni-padeborn.de.conf sites-enabled/openbot.cs.uni-padeborn.de.conf

COPY index.html /var/www/openbot.cs.uni-paderborn.de/public/

VOLUME [ "/etc/nginx/ssl" ]