/**
 * This module demonstrates the use of the typing indicator in a conversation, and when using bot.reply
 * Tell your bot "typing dialog" or "typing reply" to see this in action.
 */
const { BotkitConversation } = require("botkit");

module.exports = function(controller) {

    
    controller.hears('quick replies', 'message', async (bot, message) => {
        await bot.reply(message, {
            text: 'Here are some quick reply options',
            type: 'message', 
            quick_replies: [
                {
                    label: 'Foo',
                    description: 'foo',
                },
                {
                    label: 'Bar',
                    description: 'bar',
                }
            ]}
        );
    });

    controller.hears('ctas', 'message', async (bot, message) => {
        await bot.reply(message, {
            text: 'Here are your call to actions',
            type: 'message',
            ctas: [
                {
                    type: 'web_url',
                    label: 'The OPAL Website',
                    url: 'http://projekt-opal.de/'
                },
                {
                    type: 'web_url',
                    label: 'Fuseki Endpoint',
                    url: 'https://openbot.cs.upb.de/fuseki/'
                }
            ]
        })
    });

};