# Benchmark

This folder contains the question sets used for benchmarking the QA system and several utility scripts for formatting the question sets as well as a script for a manual benchmark. 

The files, in particular, contain:

- `benchmark_results_de.json` - the results of a manual benchmark of the QA system with the `benchmark.js` script and the german questions from the `opalbenchmark_merged.json` (the number of results was limited to 100 for all questions to keep the file size small)
- `benchmark_results_en.json` - the same but with the english translation of the questions
- `opalbenchmark_lukas.json` - the question set with annotations from Lukas exported from QUANT
- `opalbenchmark_marten.json` - the question set with annotations from Marten exported from QUANT
- `opalbenchmark_merged.json` - the question set with the annotations decided on during the merging process
- variants of the `opalbenchmark` where the results are unified as described in the thesis


scripts:

- `benchmark.js`
  - takes an `opalbenchmark.json` file as input
  - sends all questions to the specified HTTP endpoint
  - writes the results from the HTTP requests to a file
- `fix_ids.js`
  - takes two `opalbenchmark.json` files as input, one as src one as target
  - adjusts the ids of the questions from the target file so that they match the ids from the same questions from the source file
  - searches the matching questions by comparing the question strings
  - prints out the ids of the questions that could not be matched