FROM node:lts-alpine
WORKDIR /

EXPOSE 3000

COPY package*.json ./

RUN npm install

COPY bot.js .
COPY qa.js .

COPY public public
COPY sass sass
COPY features features

CMD ["node", "bot"]     