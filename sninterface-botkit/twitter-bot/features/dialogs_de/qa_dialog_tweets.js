const { BotkitConversation } = require('botkit');

const qa = require('../../qa');



module.exports = function(controller) {


    let qa_dialog = new BotkitConversation('qa_tweet', controller);


    qa_dialog.addAction('qa_tweet');

    qa_dialog.addAction('answer_thread', 'qa_tweet');


    qa_dialog.before('answer_thread',  async (convo, bot) => {
        const answer = await qa.askQuestion('test').catch(err => {
            if (err.message == 'noanswer') {
                convo.gotoThread('fail_noanswer_thread');
            } else {
                convo.gotoThread('fail_noconnect_thread')
            }
        });        
        convo.setVar('qa_answer', answer);
    });

    
    qa_dialog.addMessage({type: 'tweet', text: '{{{vars.qa_answer}}}'}, 'answer_thread');
    qa_dialog.addAction('succ_thread', 'answer_thread');



    // success thread    
    qa_dialog.addMessage({type: 'tweet', text: 'OK, hast du noch weitere Fragen?'}, 'succ_thread'); 

    // noanswer failure thread
    qa_dialog.addMessage({type: 'tweet', text: 'Sorry leider konnte ich die Frage nicht beantworten.'}, 'fail_noanswer_thread');
    qa_dialog.addMessage({type: 'tweet', text: 'Hast du trotzdem noch andere Fragen?'}, 'fail_noanswer_thread');

    // noconnect failure thread
    qa_dialog.addMessage({type: 'tweet', text: 'Sorry anscheinend ist das QA-System gerade nicht erreichbar.'}, 'fail_noconnect_thread');
    qa_dialog.addMessage({type: 'tweet', text: 'Versuch es später noch einmal.'}, 'fail_noconnect_thread');

    controller.addDialog(qa_dialog);

    controller.on(['tweet'], async(bot, message) => {
        await bot.beginDialog('qa_tweet', message);

    });

    

}
