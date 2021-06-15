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

    import PlayControl from "../PlayControl.svelte";
    import VSpace from "../VSpace.svelte";
    import { selectionStore } from "../../stores/selection.store";
    import Queries from "./Queries.svelte";
    import ScaleSelector from "./ScaleSelector.svelte";
    import GraphSize from "./GraphSize.svelte";
    import MeanLatency from "./MeanLatency.svelte";
    import NetworkActivity from "./NetworkActivity.svelte";
    import Card from "../Card.svelte";
    import SystemsMeanLatencies from "./SystemsMeanLatencies.svelte";
    import ShardDonut from "./ShardDonut.svelte";
    import Config from "./Config.svelte";
    import { configStore } from "../../stores/config.store";
    import DataComparison from "./DataComparison.svelte";
</script>

<main class="text-center h-screen w-full text-sm overflow-y-auto">
    <div class:dark={$configStore.darkMode}>
        <div class="bg-white dark:bg-gray-900 py-4 inset-0 dark:text-gray-300 h-screen overflow-y-auto">
            <div class="flex flex-row justify-center">
                <div class="w-5/6">
                    <div class="flex flex-row justify-between">
                        <div class="w-2/5">
                            <Card>
                                <Queries />
                            </Card>
                        </div>
                        <div class="w-1/5 flex flex-row justify-center">
                            <PlayControl disabled={!$selectionStore.query || !$selectionStore.connection} />
                        </div>
                        <div class="w-2/5">
                            <Card>
                                <ScaleSelector>
                                    <ShardDonut />
                                </ScaleSelector>
                            </Card>
                        </div>
                    </div>
                </div>
            </div>
            <VSpace />
            <div class="flex flex-row justify-center mb-4">
                <div class="w-5/6 flex flex-row justify-between">
                    <div class="w-1/2 mr-2">
                        <Card>
                            <NetworkActivity />
                        </Card>
                    </div>
                    <div class="w-1/2">
                        <Card>
                            <MeanLatency />
                        </Card>
                    </div>
                </div>
            </div>
            <div class="flex flex-row justify-center mb-4">
                <div class="w-5/6 flex flex-row justify-between">
                    <div class="w-1/2 mr-2">
                        <Card>
                            <DataComparison />
                        </Card>
                    </div>
                    <div class="w-1/2">
                        <Card><SystemsMeanLatencies /></Card>
                    </div>
                </div>
            </div>
            <div class="flex flex-row justify-center">
                <div class="w-5/6 flex flex-row justify-between">
                    <div class="w-1/2 mr-2">
                        <Card>
                            <GraphSize />
                        </Card>
                    </div>

                    {#if $configStore.showConfig}
                        <div class="w-1/2">
                            <Card>
                                <Config />
                            </Card>
                        </div>
                    {/if}
                </div>
            </div>
        </div>
    </div>
</main>
