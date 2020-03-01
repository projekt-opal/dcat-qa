module.exports = function(controller) {
    controller.hears(['opal?', 'opal', 'what is opal?'], ['message', 'tweet'], async (bot, message) => {
        bot.reply(message, 'OPAL means "Open Data Portal Germany" and is central platform that provides open data published by various german government agencies');
    });
}