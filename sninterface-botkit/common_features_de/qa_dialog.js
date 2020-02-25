const qa = require('../qa');
const { BotkitConversation } = require("botkit");


module.exports = function(controller) {


    let qa_dialog = new BotkitConversation('qa', controller);

    const stalling_phrases = [
        'Ich schaue mal nach...',
        'Gib mir einen Moment...',
        'Ich gucke mal eben...',
        'Warte eine Sekunde...',
        'Gute Frage! Lass mich das nachschlagen...',
        'Lass mich das nachschlagen...'
    ];

    qa_dialog.say(stalling_phrases[Math.floor(Math.random() * stalling_phrases.length)]);

    qa_dialog.addAction('qa');

    qa_dialog.addAction('answer_thread', 'qa');


    qa_dialog.before('answer_thread',  async (convo, bot) => {
        if (typeof convo.vars.tries !== 'undefined') {
            convo.vars.tries++;
        } else {
            convo.vars.tries = 0;
        }
        const answer = await qa.askQuestion('test', convo.vars.tries).catch(err => convo.gotoThread('fail_thread'));
        convo.setVar('qa_answer', answer);
    });

    
    qa_dialog.addMessage('{{vars.qa_answer}}', 'answer_thread');

    qa_dialog.addQuestion(
        { 
            text: 'War diese Antwort hilfreich?',
            quick_replies: [
                {
                    label: 'yes',
                    description: 'yes',
                    title: 'yes',
                    payload: 'yes'
                },
                {
                    label: 'no',
                    description: 'no',
                    title: 'no',
                    payload: 'no'
                }
            ]
        },
        [
            {
                pattern: 'yes',
                handler: async(res, convo, bot) => {
                    await convo.gotoThread('succ_thread');
                }
            },
            {
                pattern: 'no',
                handler: async(res, convo, bot) => {
                    await convo.gotoThread('answer_thread');
                }
            }
        ] , 'user_feedback', 'answer_thread');

    // success thread    
    qa_dialog.addMessage('OK, hast du noch weitere Fragen?', 'succ_thread'); 

    // failure thread
    qa_dialog.addMessage('Sorry leider konnte ich die Frage nicht beantworten.', 'fail_thread');
    qa_dialog.addMessage('Hast du trotzdem noch andere Fragen?', 'fail_thread');


    controller.addDialog(qa_dialog);

    controller.on(['message', 'tweet'], async(bot, message) => {
        await bot.beginDialog('qa', message);

    });

    

}
