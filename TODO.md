## Security, Tokens, etc:

- pass with environemnt variables to docker containers

## Build, CI/CD

- build containers in gitlab ci pipeline and push to gitlab docker registry
- provide docker-compose file to start nginx, sninterface and qa-system

## SN-Interface

- implement parameter that enables ngrok (auto deployment without nginx, domain, etc.)
- implement passing questions to qa system via rest api
- env variables:
  - consumer key
  - consumer secret
  - access token
  - access secret
  - webhook environment name
  - webhook url (if not defined use ngrok)
  - port (default 3000)
  - qa system url