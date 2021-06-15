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

    import * as Pancake from "@sveltejs/pancake";
    import { onMount } from "svelte";
    import { writable } from "svelte/store";
    export let data: number[];
    export let running = false;
    const numSlots = 30;
    let chartData = writable([]);

    // When running changes to true, clear history
    $: if (running) {
        chartData = initialChartData();
    }

    $: {
        // Continous run
        if (running) {
            // Grab the latest data value and put it in the chart
            const newPoint = [...data].slice(-1).map((latency, i) => ({ x: $chartData.length + i, y: latency }));
            chartData.update((curr) => curr.concat(newPoint));
        }
    }

    onMount(() => {
        const newPoints = [...data].map((latency, i) => ({ x: $chartData.length + i, y: latency }));
        chartData.set(newPoints);
    });

    function initialChartData() {
        return writable([...new Array(numSlots)].fill(0).map((y, i) => ({ x: i, y: 0 })));
    }

    const barWidth = 0.2;
    const xAxisAdjustment = -1 * (barWidth / 2);

    let vertMaxVal = 10;
    $: vertMaxVal = Math.max(...(data || []).map((c) => c)) * 1.2;
    const x1 = writable($chartData.length - numSlots);
    const x2 = writable($chartData.length);

    $: $x1 = $chartData.length - numSlots;
    $: $x2 = $chartData.length;
</script>

{#if $chartData.length}
    <div class="chart w-5/6 -ml-12">
        <div class="background">
            <Pancake.Chart x1={$x1 + xAxisAdjustment} x2={$x2} y1={0} y2={vertMaxVal}>
                <Pancake.Grid horizontal count={5} let:value>
                    <div class="grid-line horizontal border-b dark:border-gray-700 border-gray-200" />
                    <span class="y label text-gray-200 dark:text-gray-400">{value}</span>
                </Pancake.Grid>
            </Pancake.Chart>
        </div>
        <div class="foreground">
            <Pancake.Chart x1={$x1 + xAxisAdjustment} x2={$x2} y1={0} y2={vertMaxVal} clip>
                <Pancake.Columns data={$chartData} width={barWidth}>
                    <div class="column" />
                </Pancake.Columns>
            </Pancake.Chart>
        </div>
    </div>
{:else}
    <em class="pu-2 pb-3 dark:text-gray-600">Press play to view chart.</em>
{/if}

<style>
    .chart {
        position: relative;
        padding: 0 2em 2em 3em;
        box-sizing: border-box;
        height: 210px;
        max-width: 1080px;
    }
    .background,
    .foreground {
        position: absolute;
        width: 100%;
        height: 100%;
        padding: 3em 3em 2em 0;
        box-sizing: border-box;
    }

    .label {
        position: absolute;
        font-size: 14px;
        color: #666;
        line-height: 1;
        white-space: nowrap;
    }

    .y.label {
        bottom: -0.5em;
        line-height: 1;
        left: -2.5em;
    }

    .column {
        position: absolute;
        left: 0;
        box-sizing: border-box;
        height: 100%;
        background-color: #018bff;
        border-top-right-radius: 0.2em;
        border-top-left-radius: 0.2em;
        width: 100%;
    }
    .grid-line {
        position: relative;
        display: block;
    }
    .grid-line.horizontal {
        width: 100%;
        left: 0;
    }
</style>
