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

    import Donut from "../devtools-charts/Donut.svelte";
    import { connectionStores } from "../../stores/connections.store";
    import { selectionStore } from "../../stores/selection.store";

    let multiCircleThreshold = 150;
    let outerCircleShards = 0;
    let innerCircleShards = 0;
    $: numShards = $connectionStores[$selectionStore.connection]
        ? $connectionStores[$selectionStore.connection].shards
        : 0;
    $: outerCircleShards = numShards >= multiCircleThreshold ? Math.ceil(numShards / 10) : numShards;
    $: innerCircleShards = numShards >= multiCircleThreshold ? 10 : 0;
</script>

<div class="flex-auto donut-wrapper w-full grid pt-1 grid-cols-">
    {#key numShards}
        <div class="self-center justify-self-center">
            <Donut numShards={outerCircleShards} position="outer" />
        </div>
        {#if innerCircleShards > 0}
            <div class="self-center justify-self-center">
                <Donut numShards={innerCircleShards} position="inner" />
            </div>
        {/if}
        <span class="text-center self-center justify-self-center z-10 text-gray-500 dark:text-gray-300 text-base"
            >{numShards}<br />shards</span
        >
    {/key}
</div>

<style>
    .donut-wrapper > * {
        grid-column: 1/1;
        grid-row: 1/1;
    }
</style>
