const { BotkitConversation } = require('botkit');

const qa = require('../../qa');



module.exports = function(controller) {


    let qa_dialog = new BotkitConversation('qa', controller);

    const stalling_phrases = [
        'I\'ll look that up',
        'Give me a moment...',
        'Wait a second...',
        'Good question! I\'ll look that up.',
        'Let me look that up...'
    ];

    qa_dialog.say(stalling_phrases[Math.floor(Math.random() * stalling_phrases.length)]);

    qa_dialog.addAction('qa');

    qa_dialog.addAction('answer_thread', 'qa');


    qa_dialog.before('answer_thread',  async (convo, bot) => {
        const answer = await qa.askQuestion('test').catch(err => {
            if (err.message == 'noanswer') {
                convo.gotoThread('fail_noanswer_thread');
            } else {
                convo.gotoThread('fail_noconnect_thread')
            }
        });        
        convo.setVar('qa_answer', answer)
    });

    
    qa_dialog.addMessage('{{{vars.qa_answer}}}', 'answer_thread');
    qa_dialog.addAction('succ_thread', 'answer_thread');



    // success thread    
    qa_dialog.addMessage('OK, do you have more questions?', 'succ_thread'); 

    // noanswer failure thread
    qa_dialog.addMessage('Sorry unfortunately I could not answer your question.', 'fail_noanswer_thread');
    qa_dialog.addMessage('Do you have other questions anyway?', 'fail_noanswer_thread');

    // noconnect failure thread
    qa_dialog.addMessage('Sorry unfortunately the QA system is currently unavailable.','fail_noconnect_thread');
    qa_dialog.addMessage('Maybe try again later.','fail_noconnect_thread');

    controller.addDialog(qa_dialog);

    controller.on(['message'], async(bot, message) => {
        await bot.beginDialog('qa', message);

    });

    

}
