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

    import { startCase } from "lodash";
    import faker from "faker";
    import RandomBadge from "./RandomBadge.svelte";
    import { DateTime } from "luxon";

    export let date = DateTime.now();

    let achievement = startCase(`${faker.company.catchPhraseDescriptor()} ${faker.company.catchPhraseNoun()}`);
</script>

<div class="relative flex space-x-3">
    <div>
        <span class="h-8 w-8 rounded-full bg-gray-400 flex items-center justify-center ring-8 ring-white">
            <RandomBadge />
        </span>
    </div>
    <div class="min-w-0 flex-1 pt-1.5 flex justify-between space-x-4">
        <div class="">
            <p class="w-48 text-sm leading-6 text-gray-500 truncate">{achievement}</p>
        </div>
        <div class="text-right text-xs leading-6 whitespace-nowrap text-gray-400">
            <time datetime="2020-09-20">{date.toLocaleString(DateTime.DATE_MED)}</time>
        </div>
    </div>
</div>
