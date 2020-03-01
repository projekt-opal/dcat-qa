const { BotkitConversation } = require('botkit');

const qa = require('../../qa');



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
        const answer = await qa.askQuestion('test', convo.vars.tries).catch(err => {
            if (err.message == 'noanswer') {
                convo.gotoThread('fail_noanswer_thread');
            } else {
                convo.gotoThread('fail_noconnect_thread')
            }
        });
        convo.setVar('qa_answer', answer);
    });

    
    qa_dialog.addMessage('{{vars.qa_answer}}', 'answer_thread');

    qa_dialog.addQuestion(
        { 
            text: 'War diese Antwort hilfreich?',
            quick_replies: [
                {
                    title: 'ja',
                    payload: 'ja'
                },
                {

                    title: 'nein',
                    payload: 'nein'
                }
            ]
        },
        [
            {
                pattern: 'ja',
                handler: async(res, convo, bot) => {
                    await convo.gotoThread('succ_thread');
                }
            },
            {
                pattern: 'nein',
                handler: async(res, convo, bot) => {
                    await convo.gotoThread('answer_thread');
                }
            }
        ] , 'user_feedback', 'answer_thread');

    // success thread    
    qa_dialog.addMessage('OK, hast du noch weitere Fragen?', 'succ_thread'); 

    // noanswer failure thread
    qa_dialog.addMessage('Sorry leider konnte ich die Frage nicht beantworten.', 'fail_noanswer_thread');
    qa_dialog.addMessage('Hast du trotzdem noch andere Fragen?', 'fail_noanswer_thread');

    // noconnect failure thread
    qa_dialog.addMessage('Sorry anscheinend ist das QA-System gerade nicht erreichbar.','fail_noconnect_thread');
    qa_dialog.addMessage('Versuch es später noch einmal.','fail_noconnect_thread');

    controller.addDialog(qa_dialog);

    controller.on(['message', 'tweet'], async(bot, message) => {
        await bot.beginDialog('qa', message);

    });

    

}
