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

    import { statuses, runStatus } from "../../stores/run-status.store";
    import { connectionStores } from "../../stores/connections.store";
    import { selectionStore } from "../../stores/selection.store";
    import SelectionControl from "../SelectionControl.svelte";

    let multiCircleThreshold = 150;
    let outerCircleShards = 0;
    let innerCircleShards = 0;
    $: numShards = $connectionStores[$selectionStore.connection]
        ? $connectionStores[$selectionStore.connection].shards
        : 0;
    $: outerCircleShards = numShards >= multiCircleThreshold ? Math.ceil(numShards / 10) : numShards;
    $: innerCircleShards = numShards >= multiCircleThreshold ? 10 : 0;
</script>

<div class="flex flex-row justify-evenly">
    {#each Object.keys($connectionStores) as connName}
        <SelectionControl
            on:click={() => selectionStore.selectConnection(connName)}
            selected={$selectionStore.connection === connName}
            disabled={$runStatus === statuses.RUNNING}
        >
            <span class="block text-center w-full whitespace-nowrap text-xs">
                {connName}
            </span>
        </SelectionControl>
    {/each}
</div>

<slot />

<style>
</style>
