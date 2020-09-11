
const axios = require('axios');
const fs = require('fs');

const qaURL = 'http://localhost:8080/qa';

const lang = 'de';

const questions = require('./opalbenchmark_merged_18-08-20_fixed-ids.json').questions;

const results = [];

axios.interceptors.request.use(function (config) {

  config.metadata = { startTime: new Date()}
  return config;
}, function (error) {
  return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {

  response.config.metadata.endTime = new Date()
  response.duration = response.config.metadata.endTime - response.config.metadata.startTime
  return response;
}, function (error) {
  error.config.metadata.endTime = new Date();
  error.duration = error.config.metadata.endTime - error.config.metadata.startTime;
  return Promise.reject(error);
});

let answertime_total = 0;
let i = 0;

(async () => {
    for (let q of questions) {
        // if (i == 20) break;
        id = q.id
        q = q.question.find(x => { return x.language === lang}).string;
        console.log('send question: ' + q);
        const res = await  askQuestionPost(q, id);
        if (res !== null ){
          results.push(res.data);
          answertime_total += res.config.metadata.endTime - res.config.metadata.startTime
        }
        i++;
    }

    fs.writeFile('benchmark_results_de.json', JSON.stringify(results, null, 2), 'utf-8', 
        function (err) {
            if (err) return console.log(err);
            console.log('results > benchmark_results_de.json');
        }
    );
    console.log('avg answertime(in ms): ' + answertime_total / 50);
})();



/**
 * Gets answer to query from qa system.
 * @param {string} question to send to qa system
 */
async function askQuestionGet(question) {
    const requestConfig = {
      timeout: 50000,
      params: {
        question: question
      }
    };
    return new Promise((resolve, reject) => {
      axios.get(qaURL, requestConfig)
        .then(res => {
          res.data.askQuery = 'boolean' in res.data.answer ? true : false;
          res.data.answer = stringifyJSONResults(res.data.answer);
          // console.log(resolve)
          resolve(res.data);
        })
        .catch(err => {
          if (err.response && err.response.status == 500) {
            reject('noanswer');
          } else {
            reject('other');
          }
        })
    });
}

/**
 * Gets answer to query from qa system.
 * @param {string} question to send to qa system
 */
async function askQuestionPost(question, id) {
    const requestConfig = {
      timeout: 50000,
      params: {
        query: question,
        lang: lang,
        qId: id,
        resultLimit: 100
      }
    };
    return new Promise((resolve, reject) => {
      axios.post(qaURL, null, requestConfig)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
            console.error(err.response);
            resolve(null)
        })
    });
}


