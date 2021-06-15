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

    import { derived } from "svelte/store";
    import type { Readable } from "svelte/store";
    import { statuses, runStatus } from "../../stores/run-status.store";
    import { connectionStores } from "../../stores/connections.store";
    import type { MetricsStore } from "./data-comparison";
    import TextTable from "../devtools-charts/TextTable.svelte";
    import { selectionStore } from "../../stores/selection.store";

    let queryName = $selectionStore.query;

    $: if ([statuses.RUNNING, statuses.RESET].includes($runStatus)) {
        queryName = $selectionStore.query;
    }

    const store: Readable<MetricsStore[]> = derived(connectionStores, ($val) => {
        return Object.keys($val).map((key) => {
            const cStore = $val[key];
            const roundedValues = cStore.values.map(Math.round);
            const sortedValues = [...cStore.values];
            sortedValues.sort((a, b) => a - b);
            const midIndex = Math.floor(sortedValues.length / 2);
            const median =
                sortedValues.length < 1
                    ? 0
                    : sortedValues.length % 2
                    ? sortedValues[midIndex]
                    : (sortedValues[midIndex] + sortedValues[midIndex - 1]) / 2;

            return {
                name: cStore.name,
                mean: Math.round(cStore.values.reduce((a, v) => a + v, 0) / (cStore.values.length || 1)),
                min: Math.min(...roundedValues),
                max: Math.max(...roundedValues),
                median: Math.round(median),
                "data points": roundedValues.length,
            };
        });
    });
</script>

<span>Realtime latency comparison table</span>
{#if $store[0]["data points"] > 0}
    <div class="flex flex-row justify-center overflow-hidden">
        <TextTable data={$store} title={`Query: ${queryName}`} />
    </div>
{:else}
    <em class="pu-2 pb-3 dark:text-gray-600 block">Press play to view chart.</em>
{/if}
