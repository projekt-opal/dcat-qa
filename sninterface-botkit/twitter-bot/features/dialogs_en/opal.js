module.exports = function(controller) {
    controller.hears([
        /^opal\??$/i,
        /^what is opal\??$/i,
        /^what does opal mean\??$/i,
        /^what does opal stand for\??$/i,
        /^what do you mean with opal\??$/i,
        /^what is the meaning of opal\??$/i,
    ], ['message', 'tweet'], async (bot, message) => {
        bot.reply(message, 
            {
                type: message.type,
                text: 'OPAL means "Open Data Portal Germany" and is a central platform that provides open data published by various german government agencies'
            }
        );
    });
    controller.hears([
        /^open data\??$/i,
        /^what is open data\??$/i,
        /^what does open data mean\??$/i,
        /^what does open data stand for\??$/i,
        /^what do you mean with open data\??$/i,
        /^what is the meaning of open data\??$/i
    ], ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 
            {
                type: message.type, 
                text: 'Open Data is data that can be used, processed and redistributed  by everbody for whatever purpose. (https://en.wikipedia.org/wiki/Open_Data)'
            }
        )
    });
}