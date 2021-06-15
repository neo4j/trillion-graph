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

    import { StackedAreaChart } from "@carbon/charts";
    import { ScaleTypes } from "@carbon/charts/interfaces";
    import type { StackedAreaChartOptions } from "@carbon/charts/interfaces/charts";
    import type { ChartTabularData } from "@carbon/charts/interfaces/model";
    import type { Records } from "src/config/config";
    import { onMount } from "svelte";

    type Group = "Relastionships" | "Nodes";
    interface FormattedData extends ChartTabularData {
        group: Group;
        value: number;
        shardIndex: number;
    }

    export let data: Records;
    export let numShards: number;

    const VALUE_SCALE = 1;
    let mountElement: HTMLDivElement;

    const xDomain =
        numShards < 20
            ? [1, 2, 11]
            : numShards < 200
            ? [1, 10, 101]
            : numShards < 600
            ? [1, 50, 501]
            : numShards < 900
            ? [1, 80, 801]
            : [1, 10, 100, 1129];
    const options: StackedAreaChartOptions = {
        title: "",
        axes: {
            left: {
                stacked: true,
                scaleType: ScaleTypes.LINEAR,
                mapsTo: "value",
                domain: [
                    1,
                    (data.fixedNodes +
                        data.nodesPerShard * (numShards - 1) +
                        data.fixedRelationships +
                        data.relationshipsPerShard * (numShards - 1)) /
                        VALUE_SCALE,
                ],
            },
            bottom: {
                scaleType: ScaleTypes.LABELS,
                mapsTo: "shardIndex",
                domain: xDomain,
            },
        },
        legend: {
            enabled: false,
        },
        curve: "curveMonotoneX",
        height: "240px",
        width: "420px",
        color: {
            scale: {
                Nodes: "#018bff",
                Relationships: "#6ece11",
            },
        },
    };

    onMount(() => {
        new StackedAreaChart(mountElement, {
            data: formatData(),
            options,
        });
    });

    function formatData(): FormattedData[] {
        const aggregates = { nodes: 0, relationships: 0 };
        const { fixedNodes, nodesPerShard, fixedRelationships, relationshipsPerShard } = data;
        const out = [];
        out.push({ group: "Nodes", value: fixedNodes / VALUE_SCALE, shardIndex: 0 });
        aggregates.nodes += fixedNodes;
        out.push({ group: "Relationships", value: fixedRelationships / VALUE_SCALE, shardIndex: 0 });
        aggregates.relationships += fixedRelationships;

        xDomain.forEach((num) => {
            const NUM_FORUM_SHARDS = num - 1;
            aggregates.nodes = nodesPerShard * NUM_FORUM_SHARDS + fixedNodes;
            out.push({
                group: "Nodes",
                value: Math.round(aggregates.nodes / VALUE_SCALE),
                shardIndex: num,
            });

            aggregates.relationships = relationshipsPerShard * NUM_FORUM_SHARDS + fixedRelationships;
            out.push({
                group: "Relationships",
                value: Math.round(aggregates.relationships / VALUE_SCALE),
                shardIndex: num,
            });
        });
        return out;
    }
</script>

<div class="area px-2 pt-4">
    <div bind:this={mountElement} />
</div>
