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

    export let selected = false;
    export let action = () => {};

    const bgColor = selected ? "bg-gray-500 dark:bg-gray-700" : "dark:bg-gray-900";
    const textColor = selected ? "text-white dark:text-gray-900" : "dark:text-gray-700";
    const buttonClass = `inline-flex items-center px-4 py-2 text-xs font-medium ${bgColor} ${textColor} hover:text-blue-500 focus:outline-none`;
</script>

<span class="hidden sm:block">
    <button type="button" on:click={action} class={buttonClass}>
        <div class="-ml-1 mr-2 h-5 w-5">
            <slot name="icon" />
        </div>

        <slot />
    </button>
</span>

<style>
</style>
