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

    import { deburr, snakeCase } from "lodash";
    import UserHeader from "../components/profile/UserHeader.svelte";
    import AppShell from "./AppShell.svelte";
    import faker from "faker";
    import UserInformation from "../components/profile/UserInformation.svelte";
    import RecentPosts from "../components/profile/RecentPosts.svelte";
    import Timeline from "../components/profile/Awards.svelte";
    import { DateTime } from "luxon";
    import ActivityCalendar from "../components/profile/ActivityCalendar.svelte";
    import PopularTopics from "../components/profile/PopularTopics.svelte";

    let userProfile = faker.helpers.createCard(); // random contact card containing many properties
    userProfile.username = snakeCase(deburr(userProfile.name));
    userProfile.email = `${userProfile.username}@${userProfile.website}`;
    userProfile.phone = faker.phone.phoneNumberFormat(2);
    userProfile.joinDate = DateTime.fromJSDate(faker.date.recent(270));
</script>

<AppShell>
    <main class="py-10">
        <UserHeader {userProfile} />

        <div class="mt-8 max-w-3xl mx-auto grid grid-cols-3 gap-6 sm:px-6 lg:max-w-7xl grid-flow-row-dense">
            <div class="col-span-1 flex flex-col gap-6">
                <UserInformation {userProfile} />

                <Timeline />
            </div>

            <div class="col-span-2 flex flex-col gap-6">
                <RecentPosts />

                <div class="grid grid-cols-2 gap-6">
                    <ActivityCalendar />

                    <PopularTopics />
                </div>
            </div>
        </div>
    </main>
</AppShell>

<style>
</style>
