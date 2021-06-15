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

    import { queries } from "../../config/config";
    import { statuses, runStatus } from "../../stores/run-status.store";
    import { selectionStore } from "../../stores/selection.store";
    import SelectionControl from "../SelectionControl.svelte";
</script>

<div>
    {#each queries as query}
        <SelectionControl
            on:click={() => selectionStore.selectQuery(query.name)}
            selected={$selectionStore.query === query.name}
            disabled={$runStatus === statuses.RUNNING}
        >
            <div class="query-wrapper w-full h-full flex flex-row items-center">
                <span class="block text-left w-full whitespace-nowrap overflow-hidden text-xs">
                    {query.name}<br />
                    <span class="text-xs italic">{query.description}</span>
                </span>
                <button
                    type="button"
                    on:click|stopPropagation={() => navigator.clipboard.writeText(query.query)}
                    class="copy-button uppercase test-sm px-2 border border-gray-600 dark:border-blue-900 rounded-md focus:outline-none focus:ring-2 focus:bg-gray-500 dark:focus:bg-gray-900 focus:text-white"
                    >Copy</button
                >
            </div>
        </SelectionControl>
    {/each}
</div>

<style>
    .copy-button {
        display: none;
    }
    .query-wrapper:hover .copy-button {
        display: inline;
    }
</style>
