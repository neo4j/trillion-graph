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

export function tableizeObjects(arr: any[], selection?: string[], title?: string): string {
    const TOP_LEFT = "┌";
    const BOTTOM_LEFT = "└";
    const TOP_RIGHT = "┐";
    const BOTTOM_RIGHT = "┘";
    const TOP_COL = "┬";
    const BOTTOM_COL = "┴";
    const TOP_BORDER = "─";
    const BOTTOM_BORDER = "─";

    const VERTICAL_BORDER = "│";
    const HORIZONTAL_BORDER = "─";
    const LEFT_ROW = "├";
    const RIGHT_ROW = "┤";
    const CROSSING = "┼";
    const PADDING = " ";
    const NL = "\n";

    const out = [];
    const ref = arr[0];
    if (!ref || typeof ref !== "object") {
        throw new Error("Only an array of objects can be tableized");
    }
    const cols = selection || Object.keys(ref);
    let header = cols.map((key) => PADDING + key + PADDING);
    let data = arr.map((val) => {
        return cols.map((key) => PADDING + val[key] + PADDING);
    });
    const colWidths = cols.map((_, i) => Math.max(header[i].length, ...data.map((row) => row[i].length)));

    header = header.map((v, i) => `${v}${PADDING.repeat(colWidths[i])}`.substring(0, colWidths[i]));
    data = data.map((row) => row.map((v, i) => `${v}${PADDING.repeat(colWidths[i])}`.substring(0, colWidths[i])));

    // If we have a tabel title
    if (title) {
        const topRow = colWidths.map((v) => TOP_BORDER.repeat(v)).join(TOP_BORDER);
        out.push(TOP_LEFT + topRow + TOP_RIGHT); // Top line

        // Split long title in into multiple lines
        const splitRe = new RegExp(`.{1,${topRow.length - PADDING.length * 2}}`, "g");
        const titleRows = title.matchAll(splitRe);
        for (const titleRow of titleRows) {
            out.push(
                VERTICAL_BORDER +
                    (PADDING + titleRow + PADDING.repeat(topRow.length)).substring(0, topRow.length) +
                    VERTICAL_BORDER
            );
        }
        out.push(LEFT_ROW + colWidths.map((v) => HORIZONTAL_BORDER.repeat(v)).join(TOP_COL) + RIGHT_ROW); // top header border
    } else {
        // If we don't have a title, add column markings on top row
        out.push(TOP_LEFT + colWidths.map((v) => TOP_BORDER.repeat(v)).join(TOP_COL) + TOP_RIGHT); // Top line
    }
    out.push(VERTICAL_BORDER + header.join(VERTICAL_BORDER) + VERTICAL_BORDER); // header
    out.push(LEFT_ROW + colWidths.map((v) => HORIZONTAL_BORDER.repeat(v)).join(CROSSING) + RIGHT_ROW); // header border
    out.push(data.map((columns) => VERTICAL_BORDER + columns.join(VERTICAL_BORDER) + VERTICAL_BORDER).join(NL)); // data rows
    out.push(BOTTOM_LEFT + colWidths.map((v) => BOTTOM_BORDER.repeat(v)).join(BOTTOM_COL) + BOTTOM_RIGHT); // bottom border

    return out.join(NL);
}
