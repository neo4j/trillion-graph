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

    import Monitor from "./components/monitor/Monitor.svelte";
    import ProfilePage from "./pages/ProfilePage.svelte";
    import { ApplicationView, configStore } from "./stores/config.store";
    import Tailwind from "./Tailwind.svelte";
</script>

<Tailwind />
<svelte:head>
    {#if $configStore.darkMode}
        <link rel="stylesheet" href="https://unpkg.com/@carbon/charts/styles-g100.css" />
    {:else}
        <link rel="stylesheet" href="https://unpkg.com/@carbon/charts/styles.css" />
    {/if}
</svelte:head>

{#if $configStore.view === ApplicationView.PROFILE}
    <ProfilePage />
{:else}
    <Monitor />
{/if}

<style>
    @import url("https://fonts.googleapis.com/css2?family=Nunito+Sans:wght@300;400;700&display=swap");
    :global(body) {
        font-family: "Nunito Sans", sans-serif;
    }
</style>
