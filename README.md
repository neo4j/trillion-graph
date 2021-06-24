<p align="right">
  <a href="https://discord.gg/neo4j">
    <img alt="Discord" src="https://img.shields.io/discord/787399249741479977?logo=discord&logoColor=white">
  </a>
  <a href="https://community.neo4j.com">
    <img alt="Discourse users" src="https://img.shields.io/discourse/users?logo=discourse&server=https%3A%2F%2Fcommunity.neo4j.com">
  </a>
</p>

## Demo application instructions

### Overview

This repository contains the code necessary to reproduce the results for the Trillion Entity demonstration that was part of the NODES 2021 Keynote presentation. It contains the store generation code we used, the orchestration scripts for the AWS instances that are needed to run the setup, the queries we executed, and the client that performs the latency measurements. Please read this README in its entirety before proceeding, to make sure you have an understanding of the necessary steps.

### More Information

Blog post with more behind the scenes information ["Behind the Scenes of Creating the World’s Biggest Graph Database"](https://medium.com/neo4j/behind-the-scenes-of-creating-the-worlds-biggest-graph-database-cd22f477c843)

The NODES 2021 Keynote recording showing the Trillion Graph Demo live:

<div><a href="https://www.youtube.com/watch?v=4ZCs83_iHU8&t=2874s"><img src="https://user-images.githubusercontent.com/67427/123327197-73a5d780-d53a-11eb-94d4-c069328fa562.png"/></a></div>

Our CEO Emil Eifrem's Twitter Commentary:

<div><a href="https://twitter.com/emileifrem/status/1405528420402925575"><img width="300px" src="https://user-images.githubusercontent.com/67427/123326591-b6b37b00-d539-11eb-8086-3fdf25dfa92d.png"></a></div>

### How To

What you'll need:

1. _An AWS account_ with sufficient capacity for the number and type of EC2 instances you'll create, including access to S3. AWS is the default provider this application uses; it should be possible to modify it to use the cloud provider of your choice.
2. _Access to Neo4j Enterprise._ Fabric is a Neo4j Enterprise feature, which is distributed under a different license. It needs to be properly installed to your local Maven repository and you can find detailed instructions in the [Neo4j Documentation](https://neo4j.com/docs/java-reference/current/java-embedded/include-neo4j/)

The directory structure is as follows:

1. `cypher` contains the individual cypher queries that were used in the demo
2. `server` contains the data generation code and the instance orchestration
3. `client` contains the client for the latency measurements
4. `guide` contains a Neo4j Browser guide which explains the LDBC schema and queries

### Outline

Here we'll describe the basic steps you'll need to take. Detailed instructions are provided further down.

#### Familiarize yourself with the code.

The code provided should be straightforward to understand. You should take some time to familirize yourself with it, since you'll need to provide information specific to your environment. The main two files to look at are the `FabricDataGenerator` and `AmazonController` that you can find under the `server` directory. The first creates the stores both locally and remotely, and the second orchestrates the AWS Neo4j instances. They are structured as scripts, so you can modify them as you like. You will need to edit the code to execute the various steps and configure the setup to your requirements.

#### Create the stores

You should first create the Person and Template databases. The first is the full Person shard and the latter is the basis for the Forum shards. Typically, you will create these two locally, upload them to S3, and then orchestrate EC2 instances with the `AmazonController` to generate en mass Forum shards. Of course, with minimal changes, you can do everything locally, in one step, and then move the databases to the Fabric shards however you prefer.

#### Instantiate the Shards

The `AmazonController` class can be used to install and configure Neo4j and the shards. You will need to modify the code to execute the appropriate commands for your setup, but the basic AWS orchestration steps will be the same as for the store generation.

#### Build, install and run the application

The last step is to locally build and run the UI for the demo. With that, you'll be able to take latency measurements and explore the schema you built.


### [Generating the stores](stores.md)


### [AWS Instance Orchestration](orchestration.md)


### [Install, build and run the client](client.md)
