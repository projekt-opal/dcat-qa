FROM node:lts-alpine
WORKDIR /

EXPOSE 4000

COPY package*.json ./

RUN npm install

COPY web-bot.js ./
COPY qa.js ./

COPY public public
COPY sass sass
COPY common_features_de common_features_de

CMD ["node", "web-bot"]