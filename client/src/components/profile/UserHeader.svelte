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

    import { DateTime } from "luxon";
    import RandomFace from "./RandomFace.svelte";

    export let userProfile;
</script>

<div
    class="max-w-3xl mx-auto px-4 sm:px-6 md:flex md:items-center md:justify-between md:space-x-5 lg:max-w-7xl lg:px-8"
>
    <div class="flex items-center space-x-5">
        <div class="flex-shrink-0">
            <div class="relative">
                <RandomFace size={16} />
                <span class="absolute inset-0 shadow-inner rounded-full" aria-hidden="true" />
            </div>
        </div>
        <div>
            <h1 class="text-2xl font-bold text-gray-900">{userProfile.name}</h1>
            <p class="text-sm font-medium text-gray-500">
                <a href="/" class="text-gray-900">{userProfile.company.catchPhrase}</a> since
                <time datetime="2020-08-25">{userProfile.joinDate.toLocaleString(DateTime.DATE_FULL)}</time>
            </p>
        </div>
    </div>
</div>

<style>
</style>
