## Security, Tokens, etc:

- pass with environemnt variables to docker containers✅

## Build, CI/CD

- build containers in gitlab ci pipeline and push to gitlab docker registry✅
- provide docker-compose file to start nginx, sninterface and qa-system✅

## SN-Interface

- implement passing questions to qa system via rest api✅
- basic conversation module?
  - introduction✅
  - inform about capabilities✅
  - ask for feedback❌
  - chitchat?❌


## Botkit Twitter Adapter

- twitter dms quick replies, call to action, typing indication✅
- documentation
- better tweet reply threads
- more specific event names❓


# nginx 

- adjust security policy for web-bots extern js and css❌
  - downloaded js files and added to repo✅


# qa_system

ESIndex:
- connect to index -> retry loop✅
- parse and index dcat and launuts from files✅
- do automatically at start if indices dont exist already✅


QA-Pipeline:
- recognize language of question✅ (uses lingua instead of opennlp)
- annotate questions with dcat properties✅
- annotate questions with location entities✅
- annotate question with string literals extracted from the question text✅
- rate templates and question match and select best ones as template candidates✅
- build queries by filling templates in all possible ways with question properties, entities, etc.✅
  - define more rules for ratings
- send queries to sparql endpoint✅
- rate query results and select best one✅
  - more rules for rating results

Templates:

- parse templates from file and analyze properties✅
- add more templates

Web-Bot:
- better message formatting for results✅
- more results/ all results quick replies✅

Twitter-Bot:
- more results quick reply✅
- all results cta❌
- new qa dialog flow✅
- more results/ all results for tweets?❌
- 


QA-Improvements:

- add indexing and recognition of formats
- add indexing and recognition of themes✅
- add indexing and recognition of languages✅
- add tests
- add more comments
- recognize temporal words like month, year etc (in english)✅
- word stemming for better entity recognition
- add more labels to dcat properties (dcat:modified -> modified)✅
- removing stop words✅
- support for 'exists' queries
- recognition of german time entities
- recognize 'how big/the biggest file/...' -> byte size

QA-Bugs:

- recognition of relative time entities


Bot-Improvements:

- better chat interface
  - message formatting in frontend
  - design
  - language switch
  - input sanitation (html/js injection possible atm)