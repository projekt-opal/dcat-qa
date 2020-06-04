const axios = require('axios');
require('dotenv').config();


const qaURL = process.env.QA_URL;

/**
 * Gets answer to query from qa system.
 * @param {string} query to send to qa system
 */
async function askQuestion(query) {
  const requestConfig = {
    timeout: 10000
  };
  const data = {
    question: query
  };
  return new Promise((resolve, reject) => {
    axios.post(qaURL, data, requestConfig)
      .then(res => 
        resolve(stringfyResultsJSON(res.data, query))
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
function stringfyResultsJSON(results, question) {
  answersString = '';
  for (let binding of results.results.bindings) {
    for (let varName of results.head.vars) {
      if (binding[varName] !== undefined) {
        answersString += `${varName}: ${binding[varName].value}\n`
      }
    }

  }
  return `Question: ${question}\nAnswers:\n${answersString}`
}


module.exports = { askQuestion }