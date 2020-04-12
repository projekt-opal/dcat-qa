module.exports = function(controller) {
    const greetings = [
        'Hallo! Ich bin der OPAL Open Data Bot. Du kannst mir Fragen zu Metadaten in der OPAL Datenbank fragen.',
        'Hi! Ich bin der OPAL Open Data Bot und beantworte Fragen zu Metadaten in der OPAL Datenbank.',
        'Hey 👋 Der OPAL Open Data Bot hier. Ich beantworte Fragen zu Metadaten in der OPAL Datenbank.'
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