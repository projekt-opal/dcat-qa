module.exports = function(controller) {
    controller.hears(['hi','hello','howdy','hey','aloha','hola','bonjour','oi','hallo','moin'],['message', 'tweet'], async (bot, message) => {
        bot.reply(message, 'Hello, I am the OPAL open data bot! You can ask me questions about DCAT ')
    });
}