const { BotkitConversation } = require('botkit');

const qa = require('../qa');
const i18n = require('../i18n/i18n');

module.exports = function(controller) {

    let qa_dialog = new BotkitConversation('qa', controller);

    const stalling_phrases = i18n.qa_stalling_phrases;

    qa_dialog.say(stalling_phrases[Math.floor(Math.random() * stalling_phrases.length)]);

    qa_dialog.addAction('qa');

    qa_dialog.addAction('answer_thread', 'qa');


    qa_dialog.before('answer_thread',  async (convo, bot) => {
        await qa.askQuestion(convo.vars.text).then(
            answer => {
                if (answer.askQuery) {
                    convo.setVar('qa_answer', answer);
                    convo.gotoThread('ask_answer_thread')
                } else {
                    answer.answer = i18n.results + answer.answer;
                    convo.setVar('qa_answer', answer);
                }
            }).catch(err => {
                if (err == 'noanswer') {
                    convo.gotoThread('fail_noanswer_thread');
                } else {
                    convo.gotoThread('fail_noconnect_thread')
                }
            });
    });

    
    qa_dialog.addMessage('{{{vars.qa_answer.answer}}}', 'answer_thread');
    qa_dialog.addAction('succ_thread', 'answer_thread');

    qa_dialog.addMessage('{{{vars.qa_answer.answer}}}', 'ask_answer_thread');
    qa_dialog.addMessage(i18n.further_questions, 'ask_answer_thread');
    qa_dialog.addAction('complete', 'ask_answer_thread');

    // success thread    
    qa_dialog.addQuestion(
        {
            text: i18n.further_questions_or_more_results,
            quick_replies: [
                {
                    label: i18n.qa_quick_reply.more_results_label,
                    title: i18n.qa_quick_reply.more_results_label,
                    payload: i18n.qa_quick_reply.more_results_payload
                },
                {
                    label: i18n.qa_quick_reply.all_results_label,
                    title: i18n.qa_quick_reply.all_results_label,
                    payload: i18n.qa_quick_reply.all_results_payload
                },

            ]
        },
        [
            {
                pattern: i18n.qa_quick_reply.more_results_payload,
                handler: async function(response, convo, bot) {
                    const query = convo.vars.more_results ? convo.vars.more_results.query : convo.vars.qa_answer.query;
                    const results = await qa.getMoreResults(query).catch(err => {
                        if (err == 'noanswer') {
                            convo.gotoThread('fail_no_more_results_thread');
                        } else {
                            convo.gotoThread('fail_noconnect_thread')
                        }
                    });
                    results.answer = i18n.results + results.answer
                    convo.setVar('more_results', results);
                    await convo.gotoThread('more_results_thread');
                },
            },
            {
                pattern: i18n.qa_quick_reply.all_results_payload,
                handler: async function(response, convo, bot) {
                    const link = await qa.getLinkToFusekiWithQuery(convo.vars.qa_answer.query).catch(err => {
                        if (err == 'noanswer') {
                            convo.gotoThread('fail_no_more_results_thread');
                        } else {
                            convo.gotoThread('fail_noconnect_thread')
                        }
                    });
                    convo.setVar('all_results_link', link);
                    await convo.gotoThread('all_results_thread');
                },
            },
            {
                default: true,
                handler: async function(response, convo, bot) {
                    convo.vars.text = response;     
                    convo.gotoThread('qa');
                },
            }
        ],
        'answer', 'succ_thread'); 

    // more results thread
    qa_dialog.addMessage('{{{vars.more_results.answer}}}', 'more_results_thread');
    qa_dialog.addAction('succ_thread', 'more_results_thread')

    // all results thread
    qa_dialog.addMessage('[' + i18n.fuseki_link + ']({{{vars.all_results_link}}})', 'all_results_thread');
    qa_dialog.addAction('complete', 'all_results_thread')

    // no more results thread
    qa_dialog.addMessage(i18n.error.no_more_results, 'fail_no_more_results_thread');
    qa_dialog.addMessage(i18n.error.further_questions_anyway, 'fail_no_more_results_thread');
    qa_dialog.addAction('complete', 'fail_no_more_results_thread')

    // noanswer failure thread
    qa_dialog.addMessage(i18n.error.no_answer, 'fail_noanswer_thread');
    qa_dialog.addMessage(i18n.error.further_questions_anyway, 'fail_noanswer_thread');
    qa_dialog.addAction('complete', 'fail_noanswer_thread')

    // noconnect failure thread
    qa_dialog.addMessage(i18n.error.qa_not_available,'fail_noconnect_thread');
    qa_dialog.addMessage(i18n.error.try_again_later,'fail_noconnect_thread');
    qa_dialog.addAction('complete', 'fail_noconnect_thread')

    controller.addDialog(qa_dialog);

    controller.on(['message', 'tweet'], async(bot, message) => {
        await bot.beginDialog('qa', message);
    });
}
