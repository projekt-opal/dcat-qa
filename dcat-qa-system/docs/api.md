# GET /qa

## Parameters:

|name|type|required|description|example|
|---|---|---|---|---|
|question|string|x|the question that should be answered by the qa system|"What datasets exist for Rostock?"|

## Answers:

|status|body|description|
|---|---|---|
|200| results formatted as [SPARQL JSON results](https://www.w3.org/TR/rdf-sparql-json-res/)|the body contains the results found by the qa system|
|400|-|not all required paremeters were given or have a wrong format|
|500| error message | the qa system could not answer the question|

## Example:

Request:

    curl -G --data-urlencode "question=What datasets exist for Rostock?" https://openbot.cs.upb.de/qa

<details>
<summary>Answer</summary>

```json
{
  "query" : "SELECT DISTINCT ?var1\nWHERE {\n    ?var0 <http://www.w3.org/ns/dcat#dataset> ?var1.\n    ?var1 <http://purl.org/dc/terms/spatial> <http://projekt-opal.de/launuts/lau/DE/13003000>\n}",
  "answer" : {
    "head" : {
      "vars" : [ "var1" ]
    },
    "results" : {
      "bindings" : [ {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/fb6b77bc8c797ab6d4bc1efb56ed24fd"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/67eabd737bcef3d259f6aa18e7d5b750"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/3cf9ab1919a6e2aea08aafc1c8aafb6a"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/4251c78d13b7d9fcfaa879ece9468c57"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/ef367f0f4ce00589990bdcae7197fb2a"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/a1b783a8250ad622061eceda896950c1"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/9710315f09fc389a21dce3d419490e90"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/326a1a7f67dc4122a09cc18997f2f404"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/34de288190b6c3285beeaf4adfe1ea1f"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/e2dc0ca8e4ec054cf35cafd5fb8ed6bf"
        }
      } ]
    }
  }
}
```
</details>

# POST /qa

Provided for compatibilty with [GERBIl QA](https://github.com/dice-group/gerbil/wiki/Question-Answering).

## Parameters:

|name|type|required|description|example|
|---|---|---|---|---|
|query|string|x|the question that should be answered by the qa system|"What datasets exist for Rostock?"|
|lang|string|x|the language of the question|"en"|
|qId|number| |the id of the of the question that is set in the answer json| 1 |
|resultLimit|number| |the number of maximum results that should be returned| 100 |



## Answers:

|status|body|description|
|---|---|---|
|200| results formatted as [QALD JSON](https://github.com/dice-group/gerbil/wiki/Question-Answering)|the body contains the results found by the qa system|
|400|-|not all required paremeters were given or have a wrong format|
|500| error message | the qa system could not answer the question|

## Example:

Request:

    curl -X POST --data-urlencode "query=What datasets exist for Rostock?" --data-urlencode "lang=en" --data-urlencode "qId=1" --data-urlencode "resultLimit=10" https://openbot.cs.upb.de/qa

<details>
<summary>Answer</summary>

```json
{
    "questions": [
      {
        "id": "1",
        "question": [
            {
                "language": "en",
                "string": "What datasets exist for Rostock?"
            }
        ],
        "query": {
"sparql": "SELECT DISTINCT ?var1 WHERE {     ?var0 <http://www.w3.org/ns/dcat#dataset> ?var1.     ?var1 <http://purl.org/dc/terms/spatial> <http://projekt-opal.de/launuts/lau/DE/13003000> }"
        },
        "answers": [{ "head": {
    "vars": [ "var1" ]
  } ,
  "results": {
    "bindings": [
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/fb6b77bc8c797ab6d4bc1efb56ed24fd" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/67eabd737bcef3d259f6aa18e7d5b750" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/3cf9ab1919a6e2aea08aafc1c8aafb6a" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/4251c78d13b7d9fcfaa879ece9468c57" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/ef367f0f4ce00589990bdcae7197fb2a" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/a1b783a8250ad622061eceda896950c1" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/9710315f09fc389a21dce3d419490e90" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/326a1a7f67dc4122a09cc18997f2f404" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/34de288190b6c3285beeaf4adfe1ea1f" }
      } ,
      {
        "var1": { "type": "uri" , "value": "http://projekt-opal.de/dataset/e2dc0ca8e4ec054cf35cafd5fb8ed6bf" }
      }
    ]
  }
}
]
      }
    ]
}
```
</details>


# GET /qa/results

## Parameters:

|name|type|required|description|example|
|---|---|---|---|---|
|query|string|x|the SPARQL query for which more results should be fetched|


## Answers:

|status|body|description|
|---|---|---|
|200| results formatted as [SPARQL JSON results](https://www.w3.org/TR/rdf-sparql-json-res/)|the body contains the results found by the qa system|
|400|-|not all required paremeters were given or have a wrong format|
|500| error message | there are no more results|

## Example:

Request:

    curl -G --data-urlencode "query=SELECT DISTINCT ?var1 WHERE {    ?var0 <http://www.w3.org/ns/dcat#dataset> ?var1.    ?var1 <http://purl.org/dc/terms/spatial> <http://projekt-opal.de/launuts/lau/DE/13003000> } LIMIT 10"  https://openbot.cs.upb.de/qa/results

<details>
<summary>Answer</summary>

```json
{
  "query" : "SELECT DISTINCT ?var1 WHERE {    ?var0 <http://www.w3.org/ns/dcat#dataset> ?var1.    ?var1 <http://purl.org/dc/terms/spatial> <http://projekt-opal.de/launuts/lau/DE/13003000>} LIMIT 10\n OFFSET 10",
  "answer" : {
    "head" : {
      "vars" : [ "var1" ]
    },
    "results" : {
      "bindings" : [ {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/118e00ff9e61595eb3cf26a8bb433884"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/658fb60a46f08147044eb797377bab48"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/3127225904bfff9c4ba35b931748ddd8"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/51aea9fda0b32ff135902686ad0bb5a2"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/01f1f86e644dc189328255725b7aa006"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/b95dac45bdddfcb61158becf7fe5107a"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/3e19c5a606e19000349eee5cba58248c"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/0d7a032b8ba1ad1fb907b55905ab8622"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/8e3e2955586b71aee74d0e18563648d5"
        }
      }, {
        "var1" : {
          "type" : "uri",
          "value" : "http://projekt-opal.de/dataset/03e6409dad1309e6a5f70a41171d9a31"
        }
      } ]
    }
  }
}
```
</details>
