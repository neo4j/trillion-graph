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

const { tailwindExtractor } = require("tailwindcss/lib/lib/purgeUnusedStyles");

module.exports = {
    purge: {
        content: ["./src/**/*.{html,js,svelte,ts}"],
        options: {
            defaultExtractor: (content) => [
                // If this stops working, please open an issue at https://github.com/svelte-add/tailwindcss/issues rather than bothering Tailwind Labs about it
                ...tailwindExtractor(content),
                // Match Svelte class: directives (https://github.com/tailwindlabs/tailwindcss/discussions/1731)
                ...[...content.matchAll(/(?:class:)*([\w\d-/:%.]+)/gm)].map(([_match, group, ..._rest]) => group),
            ],
            safelist: [...new Array(6).fill(0).map((_, i) => `col-span-${i}`)],
            keyframes: true,
        },
    },
    darkMode: "class",
    theme: {
        extend: {
            fontFamily: [
                "-apple-system",
                "BlinkMacSystemFont",
                "Segoe UI",
                "Roboto",
                "Oxygen",
                "Ubuntu",
                "Cantarell",
                "Open Sans",
                "Helvetica Neue",
                "sans-serif",
            ],
        },
    },
    variants: {
        extend: {},
    },
    plugins: [],
};
