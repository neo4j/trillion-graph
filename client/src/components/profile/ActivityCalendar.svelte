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
    import Chart from "svelte-frappe-charts";
    import { DateTime } from "luxon";
    import { range } from "lodash";

    let data = {
        labels: ["Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat"],
        datasets: [
            {
                values: [10, 12, 3, 9, 8, 15, 9],
            },
        ],
    };
    export let dateSpan = 120;
    let endDate = DateTime.now();
    let startDate = endDate.minus({ days: dateSpan });
    let colors = ["#ebedf0", "#c0ddf9", "#73b3f3", "#3886e1", "#17459e"];

    let dataPoints = range(0, dateSpan).reduce((all, day) => {
        all[`${Math.floor(DateTime.now().minus({ days: day }).toMillis() / 1000)}`] = Math.floor(
            Math.random() * colors.length
        );
        return all;
    }, {});

    let heatmapData = {
        dataPoints,
        // object with timestamp-value pairs
        start: startDate.toJSDate(),
        end: endDate.toJSDate(), // Date objects
        countLabel: "Level",
        discreteDomains: 0, // default: 1
        colors,
    };
</script>

<ProfilePanel>
    <Chart type="heatmap" height={210} data={heatmapData} />
</ProfilePanel>
