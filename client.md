<p align="right">
  <a href="https://discord.gg/neo4j">
    <img alt="Discord" src="https://img.shields.io/discord/787399249741479977?logo=discord&logoColor=white">
  </a>
  <a href="https://community.neo4j.com">
    <img alt="Discourse users" src="https://img.shields.io/discourse/users?logo=discourse&server=https%3A%2F%2Fcommunity.neo4j.com">
  </a>
</p>


# Install, build and run the client

You need Node.js at least version 15. You can find the latest release [here](https://nodejs.org/en/download/current/) and at the time of writing it is 16.3.0.

Once Node.js is installed, you'll need to run from a terminal the following steps:

`cd client`

`npm install`

`npm run build`

`npm run serve`

Upon success, and with the default settings, you should see a message such as the following

```bash
  vite v2.1.2 build preview server running at:

  > Local:    http://localhost:5000/
```

Now you can visit `http://localhost:5000/` on your browser and see the application. You can expand the latency measuring panel by clicking on the expand arrows at the bottom right of the screen.

### Using real connections

By default the app will be configured to use mock data.  
To use real data you need to add a `.env` file in the demo app root `client/.env`.

Example contents of the `.env` file:

```
VITE_DATA_SOURCE=bolt
```

To use mock data, set `VITE_DATA_SOURCE=mock` (default).  
To use real bolt connections as the source, set `VITE_DATA_SOURCE=bolt`.

To configure what connections to use, edit the `client/src/config/connections.json` file.


### Open in Neo4j Desktop

For easy use of the included cypher files and viewing of the Neo4j Browser guides, open this project directly
in Neo4j Desktop. The project list will only show files which Desktop knows how to handle. 

1. Run Neo4j Desktop
2. From the "Projects" sidebar, click "New" then "Create project from directory"
3. Navigate to the cloned repository and select it


Neo4j Desktop will list and use files with these extensions:

- `.cypher` files which are individual cypher queries
- `.md` files which are markdown text
- `.neo4j-browser-guide` files which are slidedecks with embedded Cypher

Each of the above will open directly into Neo4j Browser. 
