const request = require('request-promise-native');
require('dotenv').config();


const qaURL = process.env.QA_URL;

/**
 * Gets answer to query from qa system.
 * @param {string} query to send to qa system
 */
async function askQuestion(query, tries) {
  const requestConfig = {
    url: qaURL,
    json: {
      question: query,
      tries: tries
    }
  };
  return new Promise((resolve, reject) => {
    request.post(requestConfig)
      .then(res => 
        resolve(stringfyResultsJSON(res))
      )
      .catch(err => {
        if (err.statusCode == 500) {
          reject('noanswer');
        } else {
          reject('other');
        }
      })
  });
  

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