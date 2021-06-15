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

    export let h = "auto";
    export let overflow = "auto";
</script>

<div
    class="bg-white dark:bg-gray-900 shadow-sm border border-gray-300 dark:border-blue-900 rounded-md w-full p-4 h-{h} overflow-y-{overflow}"
>
    <h2 class="text-base mb-2 dark:text-gray-50"><slot name="title" /></h2>
    <slot />
</div>
