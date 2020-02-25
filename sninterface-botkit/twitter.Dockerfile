FROM node:lts-alpine
WORKDIR /

EXPOSE 4001

COPY package*.json ./

RUN npm install

COPY twitter-bot.js ./
COPY qa.js ./

CMD ["node", "twitter-bot"]