const qa = require('../../qa');
const utils = require('../../utils');

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
    const quick_replies = [
        {
            label: 'opal?',
            title: 'opal?',
            payload: 'opal?'
        },
        {
            label: 'open data?',
            title: 'open data?',
            payload: 'open data?'
        },
        {
            label: 'zum beispiel?',
            title: 'zum beispiel?',
            payload: 'zum beispiel?'
        },
        {
            label: 'themen?',
            title: 'themen?',
            payload: 'themen?'
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
        /^was ist opal\??$/i,
        /^was heißt opal\??$/i,
        /^wofür steht opal\??$/i,
        /^was (meinst du|meinen sie) mit opal\??$/i,
        /^was ist mit opal gemeint\??$/i,
    ], ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 
            {
                type: message.type,
                text:'OPAL steht für "Open Data Portal Germany" und ist ein ganzheitliches Portal für offene Daten. (http://projekt-opal.de/)',
                quick_replies: quick_replies

            });    
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
                text: 'Als Open Data (aus englisch open data ‚offene Daten‘) werden Daten bezeichnet, die von jedermann zu jedem Zweck genutzt, weiterverbreitet und weiterverwendet werden dürfen. (https://de.wikipedia.org/wiki/Open_Data)',
                quick_replies: quick_replies
            })
    });
    controller.hears([
        /^zum beispiel\?$/i,
        /^zeig mir ein beispiel\?$/i,
        /^was wäre ein beispiel\?$/i,
        /^kannst du mir ein beispiel zeigen\?$/i,
    ], ['message', 'tweet'], async (bot, message) => {
        const question = questions[Math.floor(Math.random() * questions.length)];
        await qa.askQuestion(question).then(
            async answer => {
                await bot.reply(message, {
                    type: message.type,
                    text: question
                });
                await bot.say({
                    type: message.type,
                    text: utils.formatAsPre('Ergebnisse:\n' + answer.answer)
                });
            }
        ).catch(async err => {
            await bot.say({
                type: message.type,
                text: 'Sorry anscheinend ist das QA-System gerade nicht erreichbar.'
            });
        
        });
    });
    controller.hears([
        /^themen\??$/i,
        /^zu was für themen gibt es daten\??$/i,
        /^was für themenbereiche gibt es\??$/i,
    ], ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, {
            type: message.type,
            text: utils.formatAsPre(themes.join('\n'))
        });
        await bot.say(
            {
                type: message.type,
                text: 'Das sind die verfügbaren Themen.',
                quick_replies: quick_replies
            }
        );
    });
}
