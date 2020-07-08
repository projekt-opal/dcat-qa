const { BotkitConversation } = require('botkit');

const qa = require('../../qa');

const formatResults = (results) => {
    return results;
}

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
        if (convo.vars.text) {
            const answer = await qa.askQuestion(convo.vars.text).catch(err => {
                if (err == 'noanswer') {
                    convo.gotoThread('fail_noanswer_thread');
                } else {
                    convo.gotoThread('fail_noconnect_thread')
                }
            });
            answer.answer = formatResults(answer.answer);
            convo.setVar('qa_answer', answer);
        } 
    });

    
    qa_dialog.addMessage('{{{vars.qa_answer.answer}}}', 'answer_thread');
    qa_dialog.addAction('succ_thread', 'answer_thread');


    // success thread    
    qa_dialog.addQuestion(
        {
            text: 'OK, hast du noch weitere Fragen oder willst mehr Ergebnisse für deine letzte Frage sehen?',
            quick_replies: [
                {
                    label: 'Zeig mir mehr Ergebnisse',
                    title: 'Mehr Ergebnisse',
                    payload: 'Zeig mir mehr Ergebnisse'
                },
                {
                    label: 'Zeig mir alle Ergebnisse',
                    title: 'Alle Ergebnisse',
                    payload: 'Zeig mir alle Ergebnisse'
                },
            ]
        },
        [
            {
                pattern: 'Zeig mir mehr Ergebnisse',
                handler: async function(response, convo, bot) {
                    const query = convo.vars.more_results ? convo.vars.more_results.query : convo.vars.qa_answer.query;
                    const results = await qa.getMoreResults(query).catch(err => {
                        if (err == 'noanswer') {
                            convo.gotoThread('fail_no_more_results_thread');
                        } else {
                            convo.gotoThread('fail_noconnect_thread')
                        }
                    });
                    if (results.answer === '') {
                        await convo.gotoThread('fail_no_more_results_thread');
                    } else {
                        results.answer = formatResults(results.answer)
                        convo.setVar('more_results', results);
                        await convo.gotoThread('more_results_thread');
                    }
                },
            },
            {
                pattern: 'Zeig mir alle Ergebnisse',
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

    qa_dialog.addMessage('Hier kannst du dir alle Ergebnisse angucken: {{{vars.all_results_link}}}', 'all_results_thread');
    qa_dialog.addAction('complete', 'all_results_thread');

    // no more results thread
    qa_dialog.addMessage('Sorry anscheinend gibt es keine weiteren Ergebnisse.', 'fail_no_more_results_thread');
    qa_dialog.addMessage('Hast du trotzdem noch andere Fragen?', 'fail_no_more_results_thread');
    qa_dialog.addAction('complete', 'fail_no_more_results_thread');


    // noanswer failure thread
    qa_dialog.addMessage('Sorry leider konnte ich die Frage nicht beantworten.', 'fail_noanswer_thread');
    qa_dialog.addMessage('Hast du trotzdem noch andere Fragen?', 'fail_noanswer_thread');
    qa_dialog.addAction('complete', 'fail_noanswer_thread');


    // noconnect failure thread
    qa_dialog.addMessage('Sorry anscheinend ist das QA-System gerade nicht erreichbar.','fail_noconnect_thread');
    qa_dialog.addMessage('Versuch es später noch einmal.','fail_noconnect_thread');
    qa_dialog.addAction('complete', 'fail_noconnect_thread');
    
    controller.addDialog(qa_dialog);

    controller.on(['message'], async(bot, message) => {
        await bot.beginDialog('qa', message);
    });

    

}
