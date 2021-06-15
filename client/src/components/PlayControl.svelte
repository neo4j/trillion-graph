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

    import { connectionStores } from "../stores/connections.store";
    import { statuses, runStatus } from "../stores/run-status.store";

    export let disabled = false;
    let altKey = false;

    $: if ($runStatus === statuses.RUNNING) {
        connectionStores.resetAll();
        connectionStores.execAll();
    }

    function handleKeydown(event: KeyboardEvent) {
        if (event.altKey) {
            altKey = true;
        }
    }
    function handleKeyUp(event: KeyboardEvent) {
        if (!event.altKey) {
            altKey = false;
        }
    }
</script>

<svelte:window on:keydown={handleKeydown} on:keyup={handleKeyUp} />
<div class="run-control-wrapper flex flex-row justify-center items-center h-full">
    <button
        {disabled}
        on:click={() =>
            altKey
                ? runStatus.reset()
                : $runStatus === statuses.STOPPED
                ? runStatus.set(statuses.RUNNING)
                : runStatus.set(statuses.STOPPED)}
        class="rounded-full w-24 h-24 text-white bg-blue-500 dark:bg-gray-900 dark:text-blue-700 dark:hover:text-blue-500 shadow-md border border-gray-300 dark:border-blue-900 dark:hover:border-blue-700 text-6xl"
        class:pl-2={$runStatus === statuses.STOPPED && !altKey}
        class:pb-2={altKey}
        class:text-7xl={altKey}
    >
        {#if $runStatus === statuses.STOPPED && !altKey}
            ▶
        {:else if $runStatus === statuses.RUNNING && !altKey}
            ■
        {:else if altKey}
            &times;
        {/if}
    </button>
</div>

<style>
    .run-control-wrapper {
        max-width: 220px;
        min-width: 120px;
    }
    button {
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen-Sans, Ubuntu, Cantarell,
            "Helvetica Neue", sans-serif;
    }
    button:focus {
        outline: unset;
    }
    button:active {
        box-shadow: unset;
    }
    button:disabled {
        background-color: #ccc;
    }
</style>
