FROM node:lts-alpine
WORKDIR /app

EXPOSE 3000

COPY package*.json ./

COPY botbuilder-adapter-twitter botbuilder-adapter-twitter
RUN cd botbuilder-adapter-twitter && npm install && npm run build

RUN npm install

COPY bot.js .
COPY qa.js .

COPY i18n i18n
COPY features features

ENTRYPOINT ["node", "/app/bot.js"]