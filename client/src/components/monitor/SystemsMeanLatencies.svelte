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
    import SpringBars from "../devtools-charts/SpringBars.svelte";
    import { statuses, runStatus } from "../../stores/run-status.store";
    import { connectionStores } from "../../stores/connections.store";
    import type { MeanLatencyStore } from "./systems-mean-latencies";
    import { tick } from "svelte";

    let runId = 0;

    $: if ([statuses.RUNNING, statuses.RESET].includes($runStatus)) {
        updateRunId();
    }
    async function updateRunId() {
        await tick();
        runId += 1;
    }

    const valueStore: Readable<MeanLatencyStore[]> = derived(connectionStores, ($val) => {
        return Object.keys($val).map((key) => {
            const cStore = $val[key];
            return {
                name: cStore.name,
                executions: cStore.values.length,
                mean: Math.round(cStore.values.reduce((a, v) => a + v, 0) / (cStore.values.length || 1)),
            };
        });
    });
</script>

<span>Realtime latency comparison</span>
<div class="flex flex-row justify-center">
    {#key runId}
        <SpringBars dataStore={valueStore} />
    {/key}
</div>
