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
import { connectionStores } from "./connections.store";

type ConfigOptions = {
    darkMode: boolean;
    dataSource: DataSourceOptions;
    showConfig: boolean;
    view: ApplicationView;
};
export enum ApplicationView {
    MONITOR = "monitor",
    PROFILE = "porfile",
}
export enum DataSourceOptions {
    MOCK = "mock",
    BOLT = "bolt",
}

function createStore() {
    const initial: ConfigOptions = {
        darkMode: true,
        dataSource: (import.meta.env.VITE_DATA_SOURCE as DataSourceOptions) || DataSourceOptions.MOCK,
        showConfig: !!import.meta.env.VITE_SHOW_CONFIG,
        view: ApplicationView.PROFILE,
    };
    const store: Writable<ConfigOptions> = writable(initial);

    const enrichedStore = {
        ...store,
        setView: (view: ApplicationView) => {
            store.update((s) => {
                s.view = view;
                return s;
            });
        },
        toggleView: () => {
            store.update((c) => {
                c.view = c.view === ApplicationView.MONITOR ? ApplicationView.PROFILE : ApplicationView.MONITOR;
                return c;
            });
        },
        setDarkMode: () =>
            store.update((c) => {
                c.darkMode = true;
                return c;
            }),
        setLightMode: () =>
            store.update((c) => {
                c.darkMode = false;
                return c;
            }),
        toggleDarkMode: () =>
            store.update((c) => {
                c.darkMode = !c.darkMode;
                return c;
            }),
        setMock: () => {
            store.update((c) => {
                c.dataSource = DataSourceOptions.MOCK;
                return c;
            });
            connectionStores.disconnectAll();
        },
        setBolt: () => {
            store.update((c) => {
                c.dataSource = DataSourceOptions.BOLT;
                return c;
            });
            connectionStores.reloadConnections();
        },
        isMockedDataSource: () => get(store).dataSource === DataSourceOptions.MOCK,
        isNonMockedDataSource: () => get(store).dataSource !== DataSourceOptions.MOCK,
        toggleDataSource: () =>
            get(store).dataSource === DataSourceOptions.BOLT ? enrichedStore.setMock() : enrichedStore.setBolt(),
    };
    return enrichedStore;
}

const storeObj = createStore();
export const configStore = storeObj;

// @ts-ignore
window.toggleDarkMode = () => {
    storeObj.toggleDarkMode();
};

// @ts-ignore
window.toggleConfig = () => {
    storeObj.update((c) => {
        c.showConfig = !c.showConfig;
        return c;
    });
};

// @ts-ignore
window.toggleView = () => {
    storeObj.toggleView();
};

// @ts-ignore
window.setDataSource = (source: DataSourceOptions) => {
    storeObj.update((c) => {
        c.dataSource = source;
        return c;
    });
};
