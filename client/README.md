## Nodes 2021 Scaling Demo App

## .env file

You NEED to have a `.env` file in the root.
Example:

```
VITE_DB_USER=neo4j
VITE_DB_PASS=pw
VITE_DATA_SOURCE=mock
#VITE_SHOW_APP_WRAPPER=1

```

To use mock data, set `VITE_DATA_SOURCE=mock`.  
To use real bolt connections as the source, set `VITE_DATA_SOURCE=bolt`.
