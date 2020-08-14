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
    timeout: 50000,
    params: {
      question: question
    }
  };
  return new Promise((resolve, reject) => {
    axios.get(qaURL, requestConfig)
      .then(res => {
        res.data.askQuery = res.data.answer.boolean ? true : false;
        res.data.answer = stringfyResultsJSON(res.data.answer);
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
 * Fetches the next 10 results for the give query if they exist.
 * @param {string} query to fetch more results for
 */
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
 * Build a deep link to the fuseki query webinterface prefilled with the given query string.
 * @param {String} query which should be prefilled in the fuseki query webinterface
 */
async function getLinkToFusekiWithQuery(query) {
  return new Promise((resolve, reject) => {
    resolve(fusekiURL + '/dataset.html?tab=query&ds=/' + fusekiDatasetName + '&query=' + encodeURIComponent(query))
  });
}

/**
 * Formats SPARQL 1.1 Query Results JSON as string.
 */
function stringfyResultsJSON(results) {
  if (results.boolean) {
    return results.boolean ? "yes" : "no";
  }
  answersString = '';
  for (let binding of results.results.bindings) {
    for (let varName of results.head.vars) {
      if (binding[varName] !== undefined) {
        if (binding[varName].value.startsWith('http://projekt-opal.de/dataset/')) {
          answersString += `* ${varName}: [${binding[varName].value}](https://opal.demos.dice-research.org/view/datasetView?uri=${encodeURIComponent(binding[varName].value)})` + '\n'
        } else {
          answersString += `* ${varName}: ${binding[varName].value}` + '\n'
        }
        
      }
    }

  }
  return answersString;
}


module.exports = { askQuestion, getMoreResults, getLinkToFusekiWithQuery }
