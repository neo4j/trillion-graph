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

    import { GaugeChart } from "@carbon/charts";
    import { GaugeTypes } from "@carbon/charts/interfaces";
    import type { GaugeChartOptions } from "@carbon/charts/interfaces/charts";
    import { onMount } from "svelte";

    export let data = {};

    const SCALE = 3;
    let mountElement: HTMLDivElement;
    let gauge: GaugeChart;

    const options: GaugeChartOptions = {
        title: "",
        resizable: false,
        height: "150px",
        width: "300px",
        gauge: {
            type: GaugeTypes.SEMI,
            showPercentageSymbol: false,
            numberFormatter: (num: number): string => `${num * SCALE}ms`,
        },
        color: {
            scale: {
                value: "#018bff",
            },
        },
    };

    onMount(() => {
        gauge = new GaugeChart(mountElement, {
            data: formatData(data),
            options,
        });
    });

    $: if (data) {
        if (gauge) {
            gauge.model.setData(formatData(data));
        }
    }

    function formatData(inData) {
        return [{ group: "value", value: Math.round(inData.mean / SCALE) }];
    }
</script>

<div class="gauge p-2 pt-12">
    <div class="w-full" bind:this={mountElement} />
</div>
