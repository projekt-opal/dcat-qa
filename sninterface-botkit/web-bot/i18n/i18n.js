require('dotenv').config();


if (process.env.BOT_LANG) {
    try {
        const translation = require('./' + process.env.BOT_LANG + '.json');
        console.info(`Loaded ${process.env.BOT_LANG}.json`)
        module.exports = translation;
    } catch(e) {
        console.error(`The provided language ${process.env.BOT_LANG} is not supported`);
        console.info(`Loaded en.json`)
        module.exports = require('./en.json');
    }
} else {
    console.info(`Loaded en.json`)
    module.exports = require('./en.json');
}