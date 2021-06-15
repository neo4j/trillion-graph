<script lang="ts">
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

    import Switch from "../Switch.svelte";
    import { ApplicationView, configStore, DataSourceOptions } from "../../stores/config.store";
    import { connectionStores, stringifyConnections } from "../../stores/connections.store";
    import type { Connection } from "../../config/config";
    import { selectionStore } from "../../stores/selection.store";

    function handleDarkModeToggle() {
        configStore.toggleDarkMode();
    }
    function handleDataSourceToggle() {
        configStore.toggleDataSource();
    }
    function handleViewToggle() {
        configStore.toggleView();
    }
    function connectionsChanged(e: Event & { currentTarget: EventTarget & HTMLPreElement }) {
        const value = e.currentTarget.innerText;
        try {
            const parsed: Connection[] = JSON.parse(value);
            connectionStores.setConnections(parsed);
            selectionStore.ensureConnectionSelection();
        } catch (e) {
            console.log("Connections config: Not valid format.", e);
        }
    }
</script>

<span>Settings</span>
<div class="flex flex-row justify-end mt-4 mb-2">
    <span class="w-28"> Dark mode </span><Switch checked={$configStore.darkMode} on:toggle={handleDarkModeToggle} />
</div>
<div class="flex flex-row justify-end my-2">
    <span class="w-28"> Real data </span><Switch
        checked={$configStore.dataSource === DataSourceOptions.BOLT}
        on:toggle={handleDataSourceToggle}
    />
</div>
<div class="flex flex-row justify-end my-2">
    <span class="w-28"> Profile view </span><Switch
        checked={$configStore.view === ApplicationView.PROFILE}
        on:toggle={handleViewToggle}
    />
</div>
<div class="my-2 text-left">
    <span>Connection config</span>
    <div class="max-h-80 overflow-y-auto">
        <pre
            contenteditable
            class="block shadow-sm border border-gray-300 dark:border-blue-900 p-2 dark:bg-gray-800 bg-gray-200"
            on:input={connectionsChanged}>{stringifyConnections()}</pre>
    </div>
</div>
