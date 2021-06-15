<p align="right">
  <a href="https://discord.gg/neo4j">
    <img alt="Discord" src="https://img.shields.io/discord/787399249741479977?logo=discord&logoColor=white">
  </a>
  <a href="https://community.neo4j.com">
    <img alt="Discourse users" src="https://img.shields.io/discourse/users?logo=discourse&server=https%3A%2F%2Fcommunity.neo4j.com">
  </a>
</p>


# AWS Instance Orchestration

At this point, you should have created 1 Person shard dump and number of Forum shard dumps. The are also available in an S3 bucket or somewhere equivalent. It's time to install them and set up the Fabric proxies.

You will need to create AWS instances that run an appropriately configured and populated Neo4j server process. In our demo we used standalone instances, but configuring them to be in Causal Cluster mode should be relatively straightforward. You can use the `AmazonController` class again, and modify the `execute()` method to contain the configuration and setup commands as you see fit. One point of caution, the shards are created in Neo4j's dump format, so you'll need to execute the steps described in our [restore documentation](https://neo4j.com/docs/operations-manual/current/backup-restore/restore-dump/).

The Fabric proxies, if you have more than one, can be created either in a similar fashion or you can install them directly from your command line. A good starting point is, again, [the documentation](https://neo4j.com/docs/operations-manual/current/fabric/). 

In the end, the Fabric proxy Bolt URIs are the ones you'll pass along to the UI application so you can query and see the contents of your database. That is covered in the next step.