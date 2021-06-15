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

import { writable } from "svelte/store";
import type { Writable } from "svelte/store";
import { tick } from "svelte";
import { connectionStores } from "./connections.store";

export enum statuses {
    STOPPED = "stopped",
    RUNNING = "running",
    RESET = "reset",
}

interface RunStore<T> extends Writable<T> {
    reset: () => void;
}

function createStore(): RunStore<statuses> {
    const store: Writable<statuses> = writable(statuses.STOPPED);
    return {
        ...store,
        reset: async () => {
            store.set(statuses.RESET);
            connectionStores.resetAll();
            await tick();
            store.set(statuses.STOPPED);
        },
    };
}
const store = createStore();
export const runStatus = store;
