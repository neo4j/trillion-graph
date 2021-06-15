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

    import { createEventDispatcher } from "svelte";

    export let checked = false;

    const dispatch = createEventDispatcher();

    function toggled() {
        dispatch("toggle", { checked: !checked });
    }
</script>

<label class="switch">
    <input type="checkbox" on:change={toggled} bind:checked class="opacity-0 w-0 h-0" />
    <span class="slider absolute top-0 left-0 right-0 bottom-0 cursor-pointer bg-gray-300 rounded-full" />
</label>

<style>
    .switch {
        position: relative;
        display: inline-block;
        width: 40px;
        height: 22px;
    }

    .slider {
        -webkit-transition: 0.4s;
        transition: 0.4s;
    }

    .slider:before {
        position: absolute;
        content: "";
        height: 18px;
        width: 19px;
        left: 2px;
        bottom: 2px;
        background-color: white;
        -webkit-transition: 0.4s;
        transition: 0.4s;
        border-radius: 50%;
    }

    input:checked + .slider {
        background-color: #018bff;
    }

    input:checked + .slider {
        box-shadow: 0 0 1px #018bff;
    }

    input:checked + .slider:before {
        -webkit-transform: translateX(17px);
        -ms-transform: translateX(17px);
        transform: translateX(17px);
    }
</style>
