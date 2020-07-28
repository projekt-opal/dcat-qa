const { BotkitConversation } = require('botkit');

const qa = require('../../qa');
const utils = require('../../utils')


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
        await qa.askQuestion(convo.vars.text).then(
            answer => {
                if (answer.askQuery) {
                    answer.answer = utils.formatAsPre(answer.answer);
                    convo.setVar('qa_answer', answer);
                    convo.gotoThread('ask_answer_thread')
                } else {
                    answer.answer = utils.formatAsPre('Results:\n' + answer.answer);
                    convo.setVar('qa_answer', answer);
                }
            })
            .catch(err => {
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
    qa_dialog.addMessage('OK, do you have more questions?', 'ask_answer_thread');
    qa_dialog.addAction('complete', 'ask_answer_thread');


    // success thread    
    qa_dialog.addQuestion(
        {
            text: 'OK, do you have more questions or do you want to see more results for your last question?',
            quick_replies: [
                {
                    label: 'more results',
                    title: 'more results',
                    payload: 'show me more results'
                },
                {
                    label: 'all results',
                    title: 'all results',
                    payload: 'show me all results'
                },
            ]
        },
        [
            {
                pattern: 'show me more results',
                handler: async function(response, convo, bot) {
                    const query = convo.vars.more_results ? convo.vars.more_results.query : convo.vars.qa_answer.query;
                    const results = await qa.getMoreResults(query).catch(err => {
                        if (err == 'noanswer') {
                            convo.gotoThread('fail_no_more_results_thread');
                        } else {
                            convo.gotoThread('fail_noconnect_thread')
                        }
                    });
                    results.answer = utils.formatAsPre('Results:\n' + results.answer)
                    convo.setVar('more_results', results);
                    await convo.gotoThread('more_results_thread');
                },
            },
            {
                pattern: 'show me all results',
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

    qa_dialog.addMessage('{{{vars.more_results.answer}}}', 'more_results_thread');
    qa_dialog.addAction('succ_thread', 'more_results_thread')

    qa_dialog.addMessage('<a target="_blank" rel="noopener noreferrer" href="{{{vars.all_results_link}}}">Link to Fuseki with Query</a>', 'all_results_thread');
    qa_dialog.addAction('complete', 'all_results_thread')

    // no more results thread
    qa_dialog.addMessage('Sorry apparently there are no more results.', 'fail_no_more_results_thread');
    qa_dialog.addMessage('Do you have other questions anyway?', 'fail_no_more_results_thread');
    qa_dialog.addAction('complete', 'fail_no_more_results_thread')

    // noanswer failure thread
    qa_dialog.addMessage('Sorry unfortunately I could not answer your question.', 'fail_noanswer_thread');
    qa_dialog.addMessage('Do you have other questions anyway?', 'fail_noanswer_thread');
    qa_dialog.addAction('complete', 'fail_noanswer_thread');


    // noconnect failure thread
    qa_dialog.addMessage('Sorry unfortunately the QA system is currently unavailable.','fail_noconnect_thread');
    qa_dialog.addMessage('Maybe try again later.','fail_noconnect_thread');
    qa_dialog.addAction('complete', 'fail_noconnect_thread');

    controller.addDialog(qa_dialog);

    controller.on(['message'], async(bot, message) => {
        await bot.beginDialog('qa', message);

    });

    

}
