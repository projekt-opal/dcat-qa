const qa = require('../../qa');

module.exports = function(controller) {
    const greetings = [
        'Hello, I am the OPAL open data bot! You can ask me questions about the metadata in the OPAL database.',
        'Hi! I am the OPAL open data bot and answer question about the metadata in the OPAL database.',
        'Hey 👋 The OPAL open data bot here. I answer question about the metadata in the OPAL database.'
    ];
    const questions = [
        'What datasets exist for Rostock?',
        'What datasets exist with the topic transport?',
        'How many datasets exist for Bonn?',
        'For what cities is data available?'
    ];
    const themes = [
        'Econonmy, finance',
        'Agriculture, fisheries, forestry and food',
        'Education, culture and sport',
        'Energy',
        'Environment',
        'Government and public sector',
        'Health',
        'International issues',
        'Justice, legal system and public safety',
        'Regions and cities',
        'Population and society',
        'Science and technology',
        'Transport',
    ];
    const quick_replies = [
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
        },
        {
            label: 'for example?',
            description: '',
            title: 'for example?',
            payload: 'for example?'
        },
        {
            label: 'themes?',
            description: '',
            title: 'themes?',
            payload: 'themes?'
        }
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
                quick_replies: quick_replies
            }
        )
    });
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
                text: 'OPAL means "Open Data Portal Germany" and is a central platform that provides open data published by various german government agencies',
                quick_replies: quick_replies
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
                text: 'Open Data is data that can be used, processed and redistributed  by everbody for whatever purpose. (https://en.wikipedia.org/wiki/Open_Data)',
                quick_replies: quick_replies
            }
        )
    });
    controller.hears([
        /^for example\?$/i
    ], ['message', 'tweet'], async (bot, message) => {
        const question = questions[Math.floor(Math.random() * questions.length)];
        await qa.askQuestion(question).then(
            async answer => {
                await bot.reply(message, question);
                await bot.say(answer.answer);
            }
        );
        
    });
    controller.hears([
        /^themes\??$/i,
        /^what (themes|topics) does the data cover\??$/i,
        /^for what (themes|topics) is data available\??$/i,
    ], ['message', 'tweet'], async (bot, message) => {
        themes.forEach(async theme => {
            await bot.reply(message, theme)
        });
        await bot.say(
            {
                text: 'Those are the available themes.',
                quick_replies: quick_replies
            }
        )
        
    });
}