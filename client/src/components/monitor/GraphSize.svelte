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

    import { connectionStores } from "../../stores/connections.store";
    import { selectionStore } from "../../stores/selection.store";
    import StackedArea from "../devtools-charts/StackedArea.svelte";
    $: numShards = $connectionStores[$selectionStore.connection]
        ? $connectionStores[$selectionStore.connection].shards
        : 0;
    $: records = $connectionStores[$selectionStore.connection]
        ? $connectionStores[$selectionStore.connection].records
        : { fixedNodes: 0, nodesPerShard: 0, fixedRelationships: 0, relationshipsPerShard: 0 };
</script>

<div class="flex-none flex flex-row justify-center overflow-hidden">
    {#key numShards}
        <StackedArea data={records} {numShards} />
    {/key}
</div>
