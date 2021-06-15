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

/**
 * These functions are being called by the Mesh component when starting animation
 * via componentReference.startAni() or when prop 'aniauto' is provided (true)
 */

const rotationDelta = {
  slow: 0.001,
  medium: 0.005,
  fast: 0.01,
}

 export const rotateCube = (obj) => {
  let rAF = 0;
  let doRotate = false;

  function onStart() {
    startRotating();
  }

  function startRotating() {
    doRotate = true;
    rAF = requestAnimationFrame(rotate);
  }

  function rotate() {
    if (doRotate) {
      obj.rotation.x += rotationDelta.slow;
      obj.rotation.y += rotationDelta.slow;
      obj.rotation.z += rotationDelta.slow;
      rAF = requestAnimationFrame(rotate);
    }
  }

  function onDestroy() {
    doRotate = false;
    cancelAnimationFrame(rAF);
  }

  return {
    onStart: onStart,
    onDestroy: onDestroy,
  };
};

export const rotateCubeZ = (obj) => {
  let rAF = 0;
  let doRotate = false;

  function onStart() {
    startRotating();
  }

  function startRotating() {
    doRotate = true;
    rAF = requestAnimationFrame(rotate);
  }

  function rotate() {
    if (doRotate) {
      obj.rotation.z += rotationDelta.medium;
      rAF = requestAnimationFrame(rotate);
    }
  }

  function onDestroy() {
    doRotate = false;
    cancelAnimationFrame(rAF);
  }

  return {
    onStart: onStart,
    onDestroy: onDestroy,
  };
};


export const rotateCubeY = (obj) => {
  let rAF = 0;
  let doRotate = false;

  function onStart() {
    startRotating();
  }

  function startRotating() {
    doRotate = true;
    rAF = requestAnimationFrame(rotate);
  }

  function rotate() {
    if (doRotate) {
      obj.rotation.y += rotationDelta.slow;
      rAF = requestAnimationFrame(rotate);
    }
  }

  function onDestroy() {
    doRotate = false;
    cancelAnimationFrame(rAF);
  }

  return {
    onStart: onStart,
    onDestroy: onDestroy,
  };
};

export const rotateCubeXY = (obj) => {
  let rAF = 0;
  let doRotate = false;

  function onStart() {
    startRotating();
  }

  function startRotating() {
    doRotate = true;
    rAF = requestAnimationFrame(rotate);
  }

  function rotate() {
    if (doRotate) {
      obj.rotation.x += rotationDelta.medium;
      obj.rotation.y += rotationDelta.slow;
      rAF = requestAnimationFrame(rotate);
    }
  }

  function onDestroy() {
    doRotate = false;
    cancelAnimationFrame(rAF);
  }

  return {
    onStart: onStart,
    onDestroy: onDestroy,
  };
};

export const rotateCubeX = (obj) => {
  let rAF = 0;
  let doRotate = false;

  function onStart() {
    startRotating();
  }

  function startRotating() {
    doRotate = true;
    rAF = requestAnimationFrame(rotate);
  }

  function rotate() {
    if (doRotate) {
      obj.rotation.x += rotationDelta.medium;
      rAF = requestAnimationFrame(rotate);
    }
  }

  function onDestroy() {
    doRotate = false;
    cancelAnimationFrame(rAF);
  }

  return {
    onStart: onStart,
    onDestroy: onDestroy,
  };
};

export const rotateCubeFaster = (obj) => {
  let rAF = 0;
  let doRotate = false;

  function onStart() {
    startRotating();
  }

  function startRotating() {
    doRotate = true;
    rAF = requestAnimationFrame(rotate);
  }

  function rotate() {
    if (doRotate) {
      obj.rotation.x += rotationDelta.fast;
      obj.rotation.y += rotationDelta.fast;
      obj.rotation.z += rotationDelta.fast;
      rAF = requestAnimationFrame(rotate);
    }
  }

  function onDestroy() {
    doRotate = false;
    cancelAnimationFrame(rAF);
  }

  return {
    onStart: onStart,
    onDestroy: onDestroy,
  };
};

