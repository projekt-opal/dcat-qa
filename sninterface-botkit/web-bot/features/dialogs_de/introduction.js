const qa = require('../../qa');


module.exports = function(controller) {
    const greetings = [
        'Hallo! Ich bin der OPAL Open Data Bot. Du kannst mir Fragen zu Metadaten in der OPAL Datenbank fragen.',
        'Hi! Ich bin der OPAL Open Data Bot und beantworte Fragen zu Metadaten in der OPAL Datenbank.',
        'Hey 👋 Der OPAL Open Data Bot hier. Ich beantworte Fragen zu Metadaten in der OPAL Datenbank.'
        ];
    const questions = [
        'Was für Datensätze existieren für Rostock?',
        'Was für Datensätze gibt es mit dem Thema Verkehr?',
        'Wie viele Datensätze gibt es für Bonn?',
        'Zu welchen Orten gibt es Daten?'
    ];
    const themes = [
        'Wirtschaft, Finanzen',
        'Landwirtschaft, Fischerei, Forstwirtschaft, Nahrungsmittel',
        'Umwelt',
        'Bevölkerung, Gesellschaft',
        'Regierung, öffentlicher Sektor',
        'Energie',
        'Justiz, Rechtssystem, öffentliche Sicherheit',
        'Verkehr',
        'Bildung, Kultur, Sport',
        'Regionen, Städte',
        'Gesundheit',
        'Internationale Themen'
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
                    },
                    {
                        label: 'zum beispiel?',
                        description: '',
                        title: 'zum beispiel?',
                        payload: 'zum beispiel?'
                    },
                    {
                        label: 'themen?',
                        description: '',
                        title: 'themen?',
                        payload: 'themen?'
                    }
                ]
            }
        )
    });
    controller.hears([
        /^opal\??$/i,
        /^was ist opal\??$/i,
        /^was heißt opal\??$/i,
        /^wofür steht opal\??$/i,
        /^was (meinst du|meinen sie) mit opal\??$/i,
        /^was ist mit opal gemeint\??$/i,
    ], ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 'OPAL steht für "Open Data Portal Germany" und ist ein ganzheitliches Portal für offene Daten. (http://projekt-opal.de/)');
    });
    controller.hears([
        /^open data\??$/i,
        /^was ist open data\??$/i,
        /^was heißt open data\??$/i,
        /^wofür steht open data\??$/i,
        /^was (meinst du|meinen sie) mit open data\??$/i,
        /^was ist mit open data gemeint\??$/i
    ], ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 'Als Open Data (aus englisch open data ‚offene Daten‘) werden Daten bezeichnet, die von jedermann zu jedem Zweck genutzt, weiterverbreitet und weiterverwendet werden dürfen. (https://de.wikipedia.org/wiki/Open_Data)')
    });
    controller.hears([
        /^zum beispiel\?$/i
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
        /^themen\??$/i,
        /^zu was für themen gibt es daten\??$/i,
        /^was für themenbereiche gibt es\??$/i,
    ], ['message', 'tweet'], async (bot, message) => {
        themes.forEach(async theme => {
            await bot.reply(message, theme)
        });
        
    });
}
