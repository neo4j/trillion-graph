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

    import { configStore } from "../stores/config.store";

    export let selected = false;
    export let disabled = false;
</script>

<div
    on:click
    class="query-wrapper h-8 w-full p-6 dark:border-blue-900 dark:hover:bg-blue-700 hover:bg-gray-400 border hover:text-white border-gray-200 rounded-sm flex flex-row items-center"
    class:cursor-pointer={!disabled}
    class:disabled
    class:bg-gray-100={!selected && !$configStore.darkMode}
    class:bg-gray-900={!selected && $configStore.darkMode}
    class:bg-gray-400={selected && !$configStore.darkMode}
    class:bg-blue-700={selected && $configStore.darkMode}
    class:text-white={selected && !$configStore.darkMode}
    class:text-gray-300={selected && $configStore.darkMode}
    class:font-bold={selected}
    class:italic={disabled && !selected}
    class:text-gray-400={disabled && !selected}
>
    <slot />
</div>

<style>
    .disabled {
        pointer-events: none;
    }
</style>
