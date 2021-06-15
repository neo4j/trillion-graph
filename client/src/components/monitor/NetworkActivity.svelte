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
    import { connectionStores } from "../../stores/connections.store";
    import { selectionStore } from "../../stores/selection.store";
    import MovingBars from "../devtools-charts/MovingBars.svelte";
    import { runStatus, statuses } from "../../stores/run-status.store";

    let last = "";
    let runId = 0;

    $: {
        if ($selectionStore.connection || $selectionStore.query || $runStatus === statuses.RESET) {
            runId += 1;
        }
    }

    const recentValuesStore: Readable<number[]> = derived(
        [connectionStores, selectionStore],
        ([$val, $selectVal], set) => {
            if (!$selectVal.connection) {
                set([]);
                return;
            }
            const cStore = $val[$selectVal.connection];
            if (!cStore) {
                set([]);
                return;
            }
            const last30 = cStore.values.slice(-30) || [];
            // Quickly compare two small arrays of primitive values
            const last30String = "" + last30;
            if (last !== last30String) {
                last = last30String;
                set(last30);
            }
        },
        []
    );
</script>

<span>Network activity</span>
<div class="flex flex-row justify-center overflow-hidden">
    {#key runId}
        <MovingBars data={$recentValuesStore} running={$runStatus === statuses.RUNNING} />
    {/key}
</div>
