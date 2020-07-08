const axios = require('axios');
require('dotenv').config();


const qaURL = process.env.QA_URL;

const fusekiURL = process.env.FUSEKI_URL;
const fusekiDatasetName = process.env.FUSEKI_DATASET_NAME;



/**
 * Gets answer to query from qa system.
 * @param {string} question to send to qa system
 */
async function askQuestion(question) {
  const requestConfig = {
    timeout: 10000
  };
  const data = {
    question: question
  };
  return new Promise((resolve, reject) => {
    axios.post(qaURL, data, requestConfig)
      .then(res => {
          res.data.answer = stringfyResultsJSON(res.data.answer);
          resolve(res.data);
        }
      )
      .catch(err => {
        if (err.response.status == 500) {
          reject('noanswer');
        } else {
          reject('other');
        }
      })
  });
  

}

async function getMoreResults(query) {
  const requestConfig = {
    timeout: 10000,
    params: {
      query: query
    }
  };
  return new Promise((resolve, reject) => {
    axios.get(qaURL + '/results', requestConfig)
      .then(res => {
          res.data.answer = stringfyResultsJSON(res.data.answer);
          resolve(res.data);
        }
      )
      .catch(err => {
        if (err.response.status == 500) {
          reject('noanswer');
        } else {
          reject('other');
        }
      })
  });
}

async function getLinkToFusekiWithQuery(query) {
  return new Promise((resolve, reject) => {
    resolve(fusekiURL + '/dataset.html?tab=query&ds=/' + fusekiDatasetName + '&query=' + encodeURIComponent(query))
  });
}

/**
 * Formats SPARQL 1.1 Query Results JSON as string.
 */
function stringfyResultsJSON(results) {
  answersString = '';
  for (let binding of results.results.bindings) {
    for (let varName of results.head.vars) {
      if (binding[varName] !== undefined) {
        answersString += 
        `${varName}: ${binding[varName].value}` + '\n'
      }
    }

  }
  return answersString;
}


module.exports = { askQuestion, getMoreResults, getLinkToFusekiWithQuery }
