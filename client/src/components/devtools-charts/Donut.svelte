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

    import { DonutChart } from "@carbon/charts";
    import type { DonutChartOptions } from "@carbon/charts/interfaces";
    import { onMount } from "svelte";

    const positions = { OUTER: "outer", INNER: "inner" };
    export let numShards: number;
    export let position = "OUTER";

    let shardData = new Array(numShards).fill(0).map((_, i) => ({ group: `Shard ${i}`, value: 1 }));

    let mountElement: HTMLDivElement;
    const options: DonutChartOptions = {
        resizable: false,
        tooltip: { enabled: false },
        legend: { enabled: false },
        pie: {
            labels: { enabled: false },
        },
        donut: {
            center: { numberFormatter: () => "" },
        },
    };

    if (position === positions.INNER) {
        options.height = "135px";
        options.width = "135px";
    } else {
        options.height = "190px";
        options.width = "190px";
    }

    onMount(() => {
        new DonutChart(mountElement, {
            data: shardData,
            options,
        });
    });
</script>

<div class="donut">
    <div class="p-2" bind:this={mountElement} />
</div>
