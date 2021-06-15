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
    import { selectionStore } from "../../stores/selection.store";
    import { connectionStores } from "../../stores/connections.store";
    import Gauge from "../devtools-charts/Gauge.svelte";

    type SelectedMeanStore = {
        name: string;
        executions: number;
        mean: number;
    };

    const selectedMeanStore: Readable<SelectedMeanStore> = derived(
        [connectionStores, selectionStore],
        ([$val, $selectVal], set) => {
            if (!$selectVal.connection) {
                set({ name: "", executions: 0, mean: 0 });
                return;
            }
            const cStore = $val[$selectVal.connection];
            if (!cStore) {
                set({ name: "", executions: 0, mean: 0 });
                return;
            }
            set({
                name: cStore.name,
                executions: cStore.values.length,
                mean: Math.round(
                    cStore.values.reduce((a: number, v: number) => a + v, 0) / (cStore.values.length || 1)
                ),
            });
        },
        { name: "", executions: 0, mean: 0 }
    );
</script>

<span>Mean latency</span>
<div class="flex flex-row justify-center overflow-hidden">
    {#key `${$selectionStore.connection + $selectionStore.query}`}
        <Gauge data={$selectedMeanStore} />
    {/key}
</div>
