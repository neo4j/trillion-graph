## Nodes 2021 Demo App

### Installation

```
npm install
```

#### Running the app

```
npm run build
npm run serve
```

Upon success, and with the default settings, you should see a message such as the following
vite v2.1.2 build preview server running at:

> Local: http://localhost:5000/

Now you can visit http://localhost:5000/ on your browser and see application. You can expand the latency measuring panel by clicking on the expand arrows at the bottom right of the screen.

### Using real data

By default the app will be configured to use mock data.  
To use real data you need to add a `.env` file in the app root.

Example contents of the `.env` file:

```
VITE_DATA_SOURCE=bolt
```

To use mock data, set `VITE_DATA_SOURCE=mock` (default).  
To use real bolt connections as the source, set `VITE_DATA_SOURCE=bolt`.

To configure what connections to use, edit the `src/config/connections.json` file.
