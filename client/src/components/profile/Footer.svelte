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

    let year = DateTime.now().year;
</script>

<footer class="bg-gray-300 fixed inset-x-0 bottom-0">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-items-start gap-4 h-16 text-xs text-gray-400">
            <div>&copy; {year} Neo4j, Inc.</div>
            <div class="border-l-2 pl-4">
                CSS with <a href="https://tailwindcss.com" title="Tailwind CSS">Tailwind CSS</a>
            </div>
            <div class="border-l-2 pl-4">
                Some icons by <a href="https://www.freepik.com" title="Freepik">Freepik</a> from
                <a href="https://www.flaticon.com/" title="Flaticon">flaticon.com</a>
            </div>
        </div>
    </div>
</footer>

<style>
</style>
