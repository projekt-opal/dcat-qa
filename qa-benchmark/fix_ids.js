const src_benchmark = require('./opalbenchmark_lukas_18-08-20.json');
const target_benchmark = require('./opalbenchmark_merged_12-08-20.json');


function getQuestionByText(benchmark, question_text, lang) {
    for (const q of benchmark.questions) {
        _q = q.question.find(x => { return x.language === lang});
        if (_q && _q.string == question_text) {
            return q;
        }
    }
    return null;
}

let new_benchmark_marten = {
    dataset: {
        id: target_benchmark.dataset.id
    },
    questions: []
};
for (const q of target_benchmark.questions) {
    let q_lukas = getQuestionByText(src_benchmark, q.question.find(x => { return x.language === 'de'}).string, 'de');
    if (!q_lukas) {
        console.log(q.id);
    }
}
console.log("-----");

for (const q of src_benchmark.questions) {
    let q_marten = getQuestionByText(target_benchmark, q.question.find(x => { return x.language === 'de'}).string, 'de');
    if (q_marten) {
        q_marten.id = q.id;
        new_benchmark_marten.questions.push(q_marten);
    } else {
        console.log(q.id);
    }
}

fs = require('fs');
fs.writeFile('opalbenchmark_merged_18-08-20_fixed-ids.json', JSON.stringify(new_benchmark_marten), 'utf-8', 
    function (err) {
        if (err) return console.log(err);
        console.log('Hello World > helloworld.txt');
    }
);