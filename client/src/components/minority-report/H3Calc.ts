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

import * as h3 from 'h3-js';

export const geoTo3d = (radius:number, [latitude, longitude]:number[]) =>{
  const lambda = longitude * Math.PI / 180;
  const phi = latitude * Math.PI / 180;
  return {
    x: radius * Math.cos(phi) * Math.cos(lambda),
    y: radius * Math.sin(phi),
    z: -radius * Math.cos(phi) * Math.sin(lambda)
  };
}

export const h3ToPoint = (radius:number, h3Index:h3.H3Index) => {
  return geoTo3d(radius, h3.h3ToGeo(h3Index));
}

export const h3PentagonPoints = (radius:number) => {
  return h3.getPentagonIndexes(0).reduce((acc, res0Index) => { 
    return acc.concat(h3.h3ToChildren(res0Index, 0).map(h3I2 => h3ToPoint(radius, h3I2))); 
    }, 
    []) 
}

export const h3Res0Points = (radius:number, resolution = 1) => {
  return h3.getRes0Indexes().reduce((acc, res0Index) => { 
    return acc.concat(h3.h3ToChildren(res0Index, resolution).map(h3I2 => h3ToPoint(radius, h3I2))); 
    }, 
    []) 
}

export const h3ToPath = (radius:number, h3Index:h3.H3Index) => {
  return h3.h3ToGeoBoundary(h3Index).map( (latlon) => geoTo3d(radius, latlon));
}

export const h3Res0Paths = (radius:number) => {
  return h3.getRes0Indexes().map(h3Index => h3ToPath(radius, h3Index))
}