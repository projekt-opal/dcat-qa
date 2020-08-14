const qa = require('../qa');
const i18n = require('../i18n/i18n');

module.exports = function(controller) {
    const greetings = i18n.greetings;
    const questions = i18n.example_questions;
    const themes = i18n.themes;
    const quick_replies = [
        {
            label: i18n.intro_quick_reply.opal,
            title: i18n.intro_quick_reply.opal,
            payload: i18n.intro_quick_reply.opal
        },
        {
            label: i18n.intro_quick_reply.open_data,
            title: i18n.intro_quick_reply.open_data,
            payload: i18n.intro_quick_reply.open_data
        },
        {
            label: i18n.intro_quick_reply.example,
            title: i18n.intro_quick_reply.example,
            payload: i18n.intro_quick_reply.example
        },
        {
            label: i18n.intro_quick_reply.themes,
            title: i18n.intro_quick_reply.themes,
            payload: i18n.intro_quick_reply.themes
        }
    ];

    controller.hears(i18n.trigger.greeting.map(x => new RegExp(x, 'i')), ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 
            {
                type: message.type,
                text: greetings[Math.floor(Math.random() * greetings.length)],
                quick_replies: quick_replies              
            }
        )
    });
    controller.hears(i18n.trigger.intro.opal.map(x => new RegExp(x, 'i')), ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 
            {
                type: message.type,
                text: i18n.intro_answer.opal,
                quick_replies: quick_replies

            });    
        });
    controller.hears(i18n.trigger.intro.open_data.map(x => new RegExp(x, 'i')), ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, 
            {
                type: message.type,
                text: i18n.intro_answer.open_data,
                quick_replies: quick_replies
            })
    });
    controller.hears(i18n.trigger.intro.example.map(x => new RegExp(x, 'i')), ['message', 'tweet'], async (bot, message) => {
        const question = questions[Math.floor(Math.random() * questions.length)];
        await bot.reply(message, {
            type: message.type,
            text: question
        });
        await qa.askQuestion(question).then(
            async answer => {
                await bot.say({
                    type: message.type,
                    text: i18n.results + answer.answer
                });
            }
        ).catch(async err => {
            await bot.say({
                type: message.type,
                text: i18n.error.qa_not_available
            });
        
        });
    });
    controller.hears(i18n.trigger.intro.themes.map(x => new RegExp(x, 'i')), ['message', 'tweet'], async (bot, message) => {
        await bot.reply(message, {
            type: message.type,
            text: i18n.intro_answer.themes + '\n' + themes.join('\n')
        });
        await bot.say(
            {
                type: message.type,
                text: i18n.intro_answer.themes_explanation,
                quick_replies: quick_replies
            }
        );
    });
}
