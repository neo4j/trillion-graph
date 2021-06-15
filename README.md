## Demo application instructions

### Install and build

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

Now you can visit `http://localhost:5000/` on your browser and see application. You can expand the latency measuring panel by clicking on the expand arrows at the bottom right of the screen.

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
