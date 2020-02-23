/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */
module.exports = function(controller) {

    // use a function to match a condition in the message
    controller.hears(async (message) => message.text && message.text.toLowerCase() === 'foo', ['message'], async (bot, message) => {
        await bot.reply(message, 'I heard "foo" via a function test');
    });

    // use a regular expression to match the text of the message
    controller.hears(new RegExp(/^\d+$/), ['message','direct_message'], async function(bot, message) {
        await bot.reply(message,{ text: 'I heard a number using a regular expression.' });
    });

    // match any one of set of mixed patterns like a string, a regular expression
    controller.hears(['allcaps', new RegExp(/^[A-Z\s]+$/)], ['message','direct_message'], async function(bot, message) {
        await bot.reply(message,{ text: 'I HEARD ALL CAPS!' });
    });

    controller.hears('api', ['message'], async (bot, message) => {
        const res = await bot.api.get('/account/settings.json');
        await bot.reply(message, { text: res.body });
    })

    controller.hears(async (message) => message.text && message.text.toLowerCase() === '@opalbottest threadtest14', ['message'], async (bot, message) => {
        await bot.reply(message, `This placeholder text is gonna be HUGE. Lorem Ipsum better hope that there are no "tapes" of our conversations before he starts leaking to the press! You’re disgusting.\nI know words. I have the best words. I think my strongest asset maybe by far is my temperament. I have a placeholding temperament. I was going to say something extremely rough to Lorem Ipsum, to its family, and I said to myself, "I can't do it. I just can't do it. It's inappropriate. It's not nice." You know, it really doesn’t matter what you write as long as you’ve got a young, and beautiful, piece of text.`);
    });


}