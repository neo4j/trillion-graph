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

    import faker from "faker";
    import RandomFace from "./RandomFace.svelte";

    let randomUser = faker.name.findName();
</script>

<div class="flex space-x-3">
    <div class="flex-shrink-0">
        <RandomFace size={10} />
    </div>
    <div>
        <div class="text-sm">
            <a href="." class="font-medium text-gray-900">{randomUser}</a>
        </div>
        <div class="mt-1 h-10 text-sm text-gray-700 overflow-hidden overflow-ellipsis">
            <p>
                Ducimus quas delectus ad maxime totam doloribus reiciendis ex. Tempore dolorem maiores. Similique
                voluptatibus tempore non ut.
            </p>
        </div>
        <div class="mt-2 text-sm space-x-2">
            <span class="text-gray-500 font-medium">4d ago</span>
            <span class="text-gray-500 font-medium">&middot;</span>
            <button type="button" class="text-gray-900 font-medium">Reply</button>
        </div>
    </div>
</div>

<style>
</style>
