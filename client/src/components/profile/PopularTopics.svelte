<!-- src/App.svelte -->
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

    import ProfilePanel from "./ProfilePanel.svelte";
    import { WordCloudChart } from "@carbon/charts-svelte";
    import { range } from "lodash";
    import faker from "faker";

    export let topicCount = 10;
    let groups = ["A", "B", "C"];

    let wordData = range(0, topicCount).map(() => {
        const wordValue = Math.floor(Math.random() * (topicCount - 3)) + 3;
        const wordDisplay = `${faker.hacker.noun()} (${wordValue})`;
        return {
            word: wordDisplay,
            value: wordValue,
            group: groups[Math.floor(Math.random() * groups.length)],
        };
    });
</script>

<ProfilePanel>
    <div id="popular-topics">
        <WordCloudChart
            data={wordData}
            options={{
                resizable: true,
                legend: {
                    enabled: false,
                },
                tooltip: {
                    enabled: false,
                },
                color: {
                    pairing: {
                        option: 3,
                    },
                },
                height: "210px",
            }}
        />
    </div>
</ProfilePanel>

<style>
    :global(#popular-topics .bx--chart-holder) {
        background-color: white;
    }
</style>
