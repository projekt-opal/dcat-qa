const util = require('util');
const request = require('request');
require('dotenv').config();


const post = util.promisify(request.post);

const qaURL = process.env.QA_URL;

/**
 * Gets answer to query from qa system.
 * @param {string} query to send to qa system
 */
async function askQuestion(query) {
  return new Promise((resolve, reject) => {
    const requestConfig = {
      url: qaURL,
      json: {
        question: query
      }
    };

    post(requestConfig)
      .then(res => resolve(stringfyResultsJSON(res.body)))
      .catch(reject);
  })

}

/**
 * Formats SPARQL 1.1 Query Results JSON as string.
 */
function stringfyResultsJSON(results) {
  answersString = '';
  for (let binding of results.results.bindings) {
    for (let varName of results.head.vars) {
      answersString += `${varName}: ${binding[varName].value}\n`
    }

  }
  return `Question: ${results.question}\nAnswers:\n${answersString}`
}


module.exports = { askQuestion }