module.exports = function(controller) {
    const greetings = [
        'Hello, I am the OPAL open data bot! You can ask me questions about the metadata in the OPAL database.',
        'Hi! I am the OPAL open data bot and answer question about the metadata in the OPAL database.',
        'Hey 👋 The OPAL open data bot here. I answer question about the metadata in the OPAL database.'
    ];
    controller.hears([
        /^hi$/i,
        /^hello$/i,
        /^howdy$/i,
        /^hey$/i,
        /^aloha$/i,
        /^hola$/i,
        /^bonjour$/i,
        /^oi$/i,
        /^hallo$/i,
        /^moin$/i
    ],['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 
            {
                type: message.type,
                text: greetings[Math.floor(Math.random() * greetings.length)],
                quick_replies: [
                    {
                        label: 'opal?',
                        description: 'opal?',
                        title: 'opal?',
                        payload: 'opal?'
                    },
                    {
                        label: 'open data?',
                        description: 'open data?',
                        title: 'open data?',
                        payload: 'open data?'
                    }
                ]
            }
        )
    });
}