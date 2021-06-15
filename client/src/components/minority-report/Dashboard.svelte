<script>
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

    import Globe from "./Globe.svelte";
    import NetworkActivity from "../monitor/NetworkActivity.svelte";
    import GraphSize from "../monitor/GraphSize.svelte";
    import MeanLatency from "../monitor/MeanLatency.svelte";
    import DashboardPanel from "./DashboardPanel.svelte";
    import ScaleSelector from "../monitor/ScaleSelector.svelte";
    import Queries from "../monitor/Queries.svelte";
    import PlayControl from "../PlayControl.svelte";
    import { connectionStores } from "../../stores/connections.store";
    import { selectionStore } from "../../stores/selection.store";
    import ShardDonut from "../monitor/ShardDonut.svelte";

    const shardViews = {
        DONUT: "DONUT",
        GLOBE: "GLOBE",
    };
    let shardView = shardViews.GLOBE;
</script>

<div class="grid grid-cols-5 gap-4 px-8 py-2 dark:text-gray-300">
    <DashboardPanel colspan={3}>
        <NetworkActivity />
    </DashboardPanel>
    <DashboardPanel colspan={2}>
        <MeanLatency />
    </DashboardPanel>

    <DashboardPanel colspan={2}>
        <div class="grid grid-flow-row gap-4">
            <Queries />
            <div class="mt-6 flex flex-row justify-center">
                <PlayControl />
            </div>
        </div>
    </DashboardPanel>
    <DashboardPanel colspan={3}>
        <div class="grid grid-cols-1 gap-12 ">
            <ScaleSelector />

            <div class="flex flex-row">
                <GraphSize />
                {#if !shardView || shardView === shardViews.DONUT}
                    <ShardDonut />
                {:else}
                    {#key $connectionStores[$selectionStore.connection].shards}
                        <Globe satellites={$connectionStores[$selectionStore.connection].shards} />
                    {/key}
                {/if}
            </div>
        </div>
    </DashboardPanel>
</div>

<style>
</style>
