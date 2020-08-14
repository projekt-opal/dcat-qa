const { BotkitConversation } = require('botkit');

const qa = require('../qa');
const i18n = require('../i18n/i18n');

module.exports = function(controller) {


    let qa_dialog = new BotkitConversation('qa_tweet', controller);


    qa_dialog.addAction('qa_tweet');

    qa_dialog.addAction('answer_thread', 'qa_tweet');


    qa_dialog.before('answer_thread',  async (convo, bot) => {
        const question = convo.vars.text.replace(/\B@[a-z0-9_-]+/gi, '').trim();
        const answer = await qa.askQuestion(question).catch(err => {
            if (err == 'noanswer') {
                convo.gotoThread('fail_noanswer_thread');
            } else {
                convo.gotoThread('fail_noconnect_thread');
            }
        });
        const link = await qa.getLinkToFusekiWithQuery(answer.query).catch(err => {
            if (err == 'noanswer') {
                convo.gotoThread('fail_no_more_results_thread');
            } else {
                convo.gotoThread('fail_noconnect_thread');
            }
        });
        convo.setVar('all_results_link', link);
        convo.setVar('qa_answer', answer);
    });

    
    qa_dialog.addMessage({type: 'tweet', text: i18n.results + '{{{vars.qa_answer.answer}}}'}, 'answer_thread');
    qa_dialog.addMessage({type: 'tweet', text: i18n.fuseki_link + ': {{{vars.all_results_link}}}'}, 'answer_thread');
    qa_dialog.addAction('complete', 'answer_thread');



    // noanswer failure thread
    qa_dialog.addMessage({type: 'tweet', text: i18n.error.no_answer}, 'fail_noanswer_thread');
    qa_dialog.addAction('complete', 'fail_noanswer_thread');
    // noconnect failure thread
    qa_dialog.addMessage({type: 'tweet', text: i18n.error.qa_not_available + '\n' + i18n.error.try_again_later}, 'fail_noconnect_thread');
    qa_dialog.addAction('complete', 'fail_noconnect_thread');

    controller.addDialog(qa_dialog);

    controller.on(['tweet'], async(bot, message) => {
        await bot.beginDialog('qa_tweet', message);
    });

    

}
