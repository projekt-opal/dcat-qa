FROM node:lts-alpine
WORKDIR /app

EXPOSE 4001

COPY botbuilder-adapter-twitter botbuilder-adapter-twitter
RUN cd botbuilder-adapter-twitter && npm install && npm run build


COPY package*.json ./

RUN npm install

COPY bot.js .
COPY qa.js .

COPY features features

ENTRYPOINT ["node", "/app/bot.js"]