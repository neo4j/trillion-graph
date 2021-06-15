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

import { get, writable } from "svelte/store";
import type { Writable } from "svelte/store";
import { connections } from "../config/config";
import { connectionStores } from "./connections.store";

type SelectionStore = {
    query: string;
    connection: string;
};

function createStore() {
    const preSelectedConnection = connections[connections.length - 1];
    const store: Writable<SelectionStore> = writable({ query: "New Topics", connection: preSelectedConnection.name });
    return {
        ...store,
        selectQuery: (name: string) =>
            store.update((c) => {
                c.query = name;
                return c;
            }),
        selectConnection: (name: string) =>
            store.update((c) => {
                c.connection = name;
                return c;
            }),
        ensureConnectionSelection: () => {
            const currentConnectionsNames = Object.keys(get(connectionStores));
            const currentSelectedConnection = get(store).connection;
            if (!currentConnectionsNames.includes(currentSelectedConnection)) {
                store.update((c) => {
                    c.connection = currentConnectionsNames[currentConnectionsNames.length - 1];
                    return c;
                });
            }
        },
    };
}

const store = createStore();

export const selectionStore = store;
