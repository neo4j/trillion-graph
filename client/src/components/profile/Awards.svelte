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

    import { range } from "lodash";
    import { DateTime } from "luxon";

    import ProfilePanel from "./ProfilePanel.svelte";
    import RandomAchievement from "./RandomAchievement.svelte";

    let awards = range(0, 5);
</script>

<ProfilePanel>
    <h2 id="awards-title" class="text-lg font-medium text-gray-900">Awards</h2>

    <!-- Activity Feed -->
    <div class="mt-6 flow-root">
        <ul class="-mb-8">
            {#each awards as award, i}
                <li>
                    <div class="relative pb-8">
                        {#if i < awards.length - 1}
                            <span class="absolute top-4 left-4 -ml-px h-full w-0.5 bg-gray-200" aria-hidden="true" />
                        {/if}
                        <RandomAchievement
                            date={DateTime.now().minus({ days: Math.ceil(Math.random() * 7) + 7 * i })}
                        />
                    </div>
                </li>
            {/each}
        </ul>
    </div>
</ProfilePanel>

<style>
</style>
