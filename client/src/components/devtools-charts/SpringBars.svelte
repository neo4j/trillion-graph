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

    import type { Readable } from "svelte/store";
    import * as Pancake from "@sveltejs/pancake";
    import { spring } from "svelte/motion";
    import type { MeanLatencyStore } from "../monitor/systems-mean-latencies";

    let chartData = spring();
    let closest: { x: number; y: number };

    export let dataStore: Readable<MeanLatencyStore[]>;
    let hasStarted = false;

    $: if ($dataStore) {
        if (!hasStarted) {
            const totalExecs = $dataStore.reduce((a, c) => a + c.executions, 0);
            if (totalExecs > 0) {
                hasStarted = true;
            }
        }
        chartData.set($dataStore.map((connectionInfo, i) => ({ x: i, y: connectionInfo.mean })));
    }

    const xAxisAdjustment = -0.5;
    const barWidth = $dataStore.length < 2 ? 0.4 : 0.8;

    let vertMaxVal = 10;
    $: xAxisTickCount = $dataStore.length;

    $: vertMaxVal = Math.max(...$dataStore.map((c) => c.mean)) * 1.2;
</script>

{#if hasStarted}
    <div class="chart w-5/6">
        <Pancake.Chart x1={xAxisAdjustment} x2={$dataStore.length + xAxisAdjustment} y1={0} y2={vertMaxVal}>
            <Pancake.Box x1={xAxisAdjustment} x2={$dataStore.length + xAxisAdjustment} y2={vertMaxVal}>
                <div class="axes border-l border-b border-gray-300 dark:border-gray-600" />
            </Pancake.Box>
            <Pancake.Grid vertical count={xAxisTickCount} let:value>
                <span class="x label">{$dataStore[value].name}</span>
            </Pancake.Grid>
            <Pancake.Grid horizontal count={5} let:value>
                <div class="grid-line horizontal border-b border-gray-300 dark:border-gray-600" />
                <span class="y label">{value}</span>
            </Pancake.Grid>
            <Pancake.Columns data={$chartData} width={barWidth}>
                <div class="column" />
            </Pancake.Columns>

            {#if closest}
                <Pancake.Point x={closest.x} y={closest.y}>
                    <div class="annotation text-sm">
                        <span>Mean: {Math.round($dataStore[closest.x].mean)} ms</span>
                        <span>Runs: {$dataStore[closest.x].executions}</span>
                    </div>
                </Pancake.Point>
            {/if}

            <Pancake.Quadtree data={$chartData} bind:closest />
        </Pancake.Chart>
    </div>
{:else}
    <em class="pu-2 pb-3 dark:text-gray-600">Press play to view chart.</em>
{/if}

<style>
    .annotation {
        position: absolute;
        white-space: nowrap;
        top: 1.4em;
        transform: translate(-50%, -50%);
        line-height: 1.2;
        color: rgba(255, 255, 255, 0.9);
        padding: 0.2em 0.4em;
        pointer-events: none;
    }
    .annotation span {
        display: block;
    }
    .chart {
        padding: 3em 2em 2em 3em;
        box-sizing: border-box;
        height: 250px;
        max-width: 1080px;
    }

    .axes {
        width: 100%;
        height: 100%;
        opacity: 0.6;
    }

    .label {
        opacity: 0.6;
    }

    .y.label {
        position: absolute;
        left: -2.5rem;
        width: 2rem;
        text-align: right;
        bottom: -0.5em;
    }

    .x.label {
        position: absolute;
        width: 8rem;
        left: -4rem;
        bottom: -2rem;
        font-family: sans-serif;
        text-align: center;
    }

    .column {
        height: 100%;
        background-color: #018bff;
        border-top-right-radius: 0.2em;
        border-top-left-radius: 0.2em;
    }
    .column:hover {
        background-color: rgb(0, 108, 197);
        border: 1px solid #018bff;
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
