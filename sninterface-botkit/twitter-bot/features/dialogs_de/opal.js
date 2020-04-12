module.exports = function(controller) {
    controller.hears([
        /^opal\??$/i,
        /^was ist opal\??$/i,
        /^was heißt opal\??$/i,
        /^wofür steht opal\??$/i,
        /^was (meinst du|meinen sie) mit opal\??$/i,
        /^was ist mit opal gemeint\??$/i,
    ], ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 
            {
                type: message.type,
                text: 'OPAL steht für "Open Data Portal Germany" und ist ein ganzheitliches Portal für offene Daten. (http://projekt-opal.de/)'
            }
        );
    });
    controller.hears([
        /^open data\??$/i,
        /^was ist open data\??$/i,
        /^was heißt open data\??$/i,
        /^wofür steht open data\??$/i,
        /^was (meinst du|meinen sie) mit open data\??$/i,
        /^was ist mit open data gemeint\??$/i
    ], ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 
            {
                type: message.type, 
                text: 'Als Open Data (aus englisch open data ‚offene Daten‘) werden Daten bezeichnet, die von jedermann zu jedem Zweck genutzt, weiterverbreitet und weiterverwendet werden dürfen. (https://de.wikipedia.org/wiki/Open_Data)'
            }
        )
    });
}