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

    import Navigation from "../components/profile/Navigation.svelte";
    import Overlay from "../components/Overlay.svelte";
    import Footer from "../components/profile/Footer.svelte";
</script>

<div>
    <Navigation />

    <main>
        <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
            <slot />
        </div>
    </main>

    <Footer />
    <Overlay />
</div>
