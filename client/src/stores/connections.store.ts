/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import type { Driver } from "neo4j-driver/lib/browser/neo4j-web";
import neo4j from "neo4j-driver/lib/browser/neo4j-web";
import { writable, get } from "svelte/store";
import type { Writable } from "svelte/store";
import { connections, queries } from "../config/config";
import type { Connection } from "../config/config";
import { runStatus, statuses } from "./run-status.store";
import { selectionStore } from "./selection.store";
import { configStore } from "./config.store";
import { tableizeObjects } from "../utils/tableize-objects.utils";

interface LiveConnection extends Connection {
    values: number[];
    exec: () => void;
    driver: Driver;
}
type ConnectionStore = {
    [key: string]: LiveConnection;
};
interface ConnectionStoreWrapper<T> extends Writable<T> {
    execAll: () => void;
    addValue: (name: string, value: number) => void;
    resetAll: () => void;
    disconnectAll: () => void;
    setConnections: (connections: Connection[]) => void;
    reloadConnections: () => void;
}

const sleep = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));
const runQuery = async (conn: LiveConnection) => {
    const currentSelectionStore = get(selectionStore);
    const query = queries.find((q) => q.name === currentSelectionStore.query);

    if (configStore.isNonMockedDataSource()) {
        const session = conn.driver.session({ defaultAccessMode: neo4j.session.READ, database: query.db });
        const result = await session.run(query.query, query.params);
        session.close();
        const { resultAvailableAfter, resultConsumedAfter } = result.summary;
        return resultAvailableAfter.toNumber() + resultConsumedAfter.toNumber() + 1;
    } else {
        const wait = Math.random() * 100 + 10; // 10ms -> 100ms
        await sleep(wait);
        return wait;
    }
};
const exec = async (conn: LiveConnection) => {
    const time = await runQuery(conn);
    if (get(runStatus) === statuses.RUNNING) {
        connectionStores.addValue(conn.name, time);
        await sleep(100);
        exec(conn);
    }
};

function setupStore(): ConnectionStoreWrapper<ConnectionStore> {
    const connectionStoreInstance: Writable<ConnectionStore> = writable(formatConnections(connections));
    const storeObj: ConnectionStoreWrapper<ConnectionStore> = {
        ...connectionStoreInstance,
        execAll: () => {
            const conns = get(connectionStoreInstance);
            Object.keys(conns).forEach((key) => conns[key].exec());
        },
        addValue: (name: string, val: number) => {
            connectionStoreInstance.update((conns) => {
                conns[name].values.push(val);
                return conns;
            });
        },
        resetAll: () => {
            connectionStoreInstance.update((conns) => {
                Object.keys(conns).forEach((name) => (conns[name].values = []));
                return conns;
            });
        },
        disconnectAll: () => {
            connectionStoreInstance.update((conns) => {
                Object.keys(conns).forEach((name) => {
                    if (conns[name].driver && conns[name].driver.close) {
                        conns[name].driver.close();
                    }
                    return conns[name];
                });
                return conns;
            });
        },
        setConnections: (connectionsArray: Connection[]) => {
            storeObj.disconnectAll();
            connectionStoreInstance.set(formatConnections(connectionsArray));
        },
        reloadConnections: () => {
            storeObj.setConnections(connections);
        },
    };
    return storeObj;
}

function formatConnections(connectionsInput: Connection[]): ConnectionStore {
    return connectionsInput.reduce((all, c) => {
        let connObj: LiveConnection = {
            ...c,
            values: [],
            driver:
                configStore.isNonMockedDataSource() && c.bolt
                    ? neo4j.driver(c.bolt, c.username ? neo4j.auth.basic(c.username, c.password) : {})
                    : null,
            exec: () => {},
        };
        connObj.exec = () => exec(connObj);
        all[c.name] = connObj;
        return all;
    }, {});
}
export function stringifyConnections() {
    const latest = get(stores);
    if (!latest) {
        return "[]";
    }
    let out = [];
    Object.keys(latest).forEach((name) => {
        let cp = { ...latest[name] };
        delete cp.driver;
        delete cp.values;
        delete cp.exec;
        out.push(cp);
    });
    return JSON.stringify(out, null, 2);
}

const stores = setupStore();

export const connectionStores = stores;

//@ts-ignore
window.logTable = () => {
    const c = get(stores);
    const out = Object.keys(c).map((name) => {
        const conn = c[name];
        const roundedValues = conn.values.map(Math.round);
        const sortedValues = [...roundedValues];
        sortedValues.sort((a, b) => a - b);
        const midIndex = Math.floor(sortedValues.length / 2);
        const data = {
            name: conn.name,
            avg: conn.values.reduce((a, v) => a + v, 0) / conn.values.length,
            min: Math.min(...roundedValues),
            max: Math.max(...roundedValues),
            median:
                sortedValues.length % 2
                    ? sortedValues[midIndex]
                    : (sortedValues[midIndex] + sortedValues[midIndex + 1]) / 2,
            dataPoints: roundedValues.length,
            "values (subset)": roundedValues.slice(0, 10).join(", "),
        };
        return data;
    });
    console.log(tableizeObjects(out, ["name", "avg", "min", "max", "median", "dataPoints"]));
};

// @ts-ignore
window.logJson = () => {
    const currentConnectionStore = get(stores);
    const currentSelectionStore = get(selectionStore);
    const out = Object.keys(currentConnectionStore).reduce(
        (all, name) => {
            all.servers[name] = {
                name,
                values: currentConnectionStore[name].values,
            };
            return all;
        },
        { servers: {}, query: currentSelectionStore.query }
    );

    console.log(JSON.stringify(out));
};
