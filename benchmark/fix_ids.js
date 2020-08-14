const benchmark_lukas = require('./opalbenchmark_lukas_18-08-20.json');
const benchmark_marten = require('./opalbenchmark_marten_18-08-20.json');


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
        id: benchmark_marten.dataset.id
    },
    questions: []
};
for (const q of benchmark_marten.questions) {
    let q_lukas = getQuestionByText(benchmark_lukas, q.question.find(x => { return x.language === 'de'}).string, 'de');
    if (!q_lukas) {
        console.log(q.id);
    }
}
console.log("-----");

for (const q of benchmark_lukas.questions) {
    let q_marten = getQuestionByText(benchmark_marten, q.question.find(x => { return x.language === 'de'}).string, 'de');
    if (q_marten) {
        q_marten.id = q.id;
        new_benchmark_marten.questions.push(q_marten);
    } else {
        console.log(q.id);
    }
}

fs = require('fs');
fs.writeFile('opalbenchmark_marten_18-08-20_fixed.json', JSON.stringify(new_benchmark_marten), 'utf-8', 
    function (err) {
        if (err) return console.log(err);
        console.log('Hello World > helloworld.txt');
    }
);