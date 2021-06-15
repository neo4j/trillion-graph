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

    import {
        Canvas,
        Scene,
        PerspectiveCamera,
        DirectionalLight,
        AmbientLight,
        BoxBufferGeometry,
        CylinderBufferGeometry,
        DodecahedronBufferGeometry,
        Mesh,
        MeshStandardMaterial,
        MeshPhysicalMaterial,
        WebGLRenderer,
        OrbitControls,
        FrontSide,
        Euler,
        Vector3,
        Quaternion,
        Matrix4,
        StaticReadUsage,
        BackSide,
        Object3D,
        MathUtils,
    } from "svelthree";
    import { interpolateNumber, interpolateNumberArray } from "d3-interpolate";
    import { sampleSize } from "lodash";

    import { rotateCubeZ, rotateCubeX, rotateCubeY, rotateCubeXY } from "./animations.js";

    import { h3Res0Points, h3PentagonPoints } from "./H3Calc";

    import { statuses, runStatus } from "../../stores/run-status.store";

    export let satellites = 100;

    let fabricSizeInterpolator = interpolateNumber(3, 5);
    let shardRadiusInterpolator = interpolateNumber(10, 12);
    let shardShapeInterpolator = interpolateNumberArray([1.0, 1.5, 1.5], [0.25, 0.5, 0.45]);
    let h3Resolution = satellites < 122 ? 0 : 1;

    // let cubeGeometry = new BoxBufferGeometry(1, 1, 1);
    let shardMaterial = new MeshStandardMaterial();
    let shardShape = shardShapeInterpolator(satellites / 1000);
    let shardGeometry = new CylinderBufferGeometry(shardShape[0], shardShape[1], shardShape[2], 6);
    shardGeometry.translate(0, 0, 0);
    shardGeometry.rotateX(MathUtils.degToRad(90));

    let fabricGeometry = new DodecahedronBufferGeometry(fabricSizeInterpolator(satellites / 1000), 0);
    let fabricMaterial = new MeshPhysicalMaterial({
        map: null,
        color: 0xffffff,
        metalness: 1,
        roughness: 0,
        opacity: 0.75,
        side: BackSide,
        transparent: true,
        envMapIntensity: 10,
        premultipliedAlpha: true,
    });

    let h3Indexes =
        satellites > 10
            ? h3Res0Points(shardRadiusInterpolator(satellites / 1000), h3Resolution)
            : h3PentagonPoints(shardRadiusInterpolator(satellites / 1000));
    let regions = sampleSize(h3Indexes, satellites); //.slice(0, satellites);
</script>

<div class="flex-auto">
    <Canvas class="mx-auto" let:sti w={200} h={200}>
        <Scene {sti} let:scene id="scene1" props={{ background: 0x111827 }}>
            <PerspectiveCamera
                {scene}
                id="cam1"
                pos={[0, 30, 30]}
                lookAt={[0, 0, 0]}
                props={{
                    zoom: 1.5,
                }}
            />
            <AmbientLight {scene} intensity={1.25} />
            <DirectionalLight {scene} pos={[0, 30, 30]} />

            <Mesh
                {scene}
                let:parent
                geometry={fabricGeometry}
                material={fabricMaterial}
                mat={{ flatShading: true, roughness: 0.25, metalness: 0.5, color: 0x0099ee }}
                pos={[0, 0, 0]}
                scale={[1, 1, 1]}
                animation={rotateCubeY}
                aniauto={$runStatus !== statuses.RUNNING}
            >
                {#each regions as region}
                    <Mesh
                        {scene}
                        {parent}
                        geometry={shardGeometry}
                        material={shardMaterial}
                        mat={{ flatShading: true, roughness: 0.25, metalness: 0.5, color: 0x003eff }}
                        pos={[region.x, region.y, region.z]}
                        scale={[1, 1, 1]}
                        animation={satellites > 200 ? rotateCubeXY : rotateCubeZ}
                        aniauto={$runStatus !== statuses.RUNNING}
                        props={{
                            lookAt: [0, 0, 0],
                            up: [0, 0, 1],
                        }}
                    />
                {/each}
            </Mesh>

            <OrbitControls
                {scene}
                enableDamping
                props={{
                    enableZoom: false,
                    enablePan: false,
                    autoRotateSpeed: 0.5,
                }}
            />
        </Scene>

        <WebGLRenderer {sti} sceneId="scene1" camId="cam1" config={{ antialias: true, alpha: true }} />
    </Canvas>
    <p class="text-center pt-4">{satellites} Shards</p>
</div>
