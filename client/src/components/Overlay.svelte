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

    import { fly } from "svelte/transition";
    import OverlayToolbar from "./OverlayToolbar.svelte";
    import ToolbarButton from "./ToolbarButton.svelte";

    import MinorityReportDashboard from "./minority-report/Dashboard.svelte";
    //import MeanLatencyDashboard from "./monitor/SystemsMeanLatencies.svelte";
    import MonitorDashboard from "./monitor/Monitor.svelte";
    import { configStore } from "../stores/config.store";

    export let open = false;

    const openOverlay = () => {
        open = true;
    };

    const closeOverlay = () => {
        open = false;
    };

    const views = {
        MEAN_LATENCY: "MEAN_LATENCY",
        MONITOR: "MONITOR",
        MINORITY: "MINORITY",
    };
    let view = views.MINORITY;

    let mode = "";
    $: mode = $configStore.darkMode ? "dark" : "";
</script>

<section
    class="pointer-events-none fixed inset-0 overflow-hidden"
    aria-labelledby="slide-over-title"
    role="dialog"
    aria-modal="true"
>
    <div class="absolute inset-0 overflow-hidden">
        <!-- Background overlay, show/hide based on slide-over state. -->
        <div class="absolute inset-0" aria-hidden="true" />

        {#if open}
            <div class="fixed inset-0 pt-16 h-full w-screen flex overflow-hidden">
                <div
                    class="{mode} w-screen max-h-2xl pointer-events-auto"
                    transition:fly={{ y: 1200, duration: 500, opacity: 1.0 }}
                >
                    <div class="h-full flex flex-col border-t-2 border-black bg-gray-100 dark:bg-gray-900">
                        <OverlayToolbar handleClose={closeOverlay} />

                        <div class="relative flex-1">
                            <div class="absolute inset-0">
                                {#if !view || view === views.MEAN_LATENCY}
                                    <MonitorDashboard />
                                {:else if view === views.MONITOR}
                                    <MonitorDashboard />
                                {:else if view === views.MINORITY}
                                    <MinorityReportDashboard />
                                {/if}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        {:else}
            <div class="fixed w-12 h-12 bottom-0 right-0 pointer-events-auto">
                <ToolbarButton action={openOverlay}>
                    <svg
                        slot="icon"
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-6 w-6"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                    >
                        <path
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-width="2"
                            d="M5 11l7-7 7 7M5 19l7-7 7 7"
                        />
                    </svg>
                </ToolbarButton>
            </div>
        {/if}
    </div>
</section>

<style>
</style>
