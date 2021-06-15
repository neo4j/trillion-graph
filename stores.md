<p align="right">
  <a href="https://discord.gg/neo4j">
    <img alt="Discord" src="https://img.shields.io/discord/787399249741479977?logo=discord&logoColor=white">
  </a>
  <a href="https://community.neo4j.com">
    <img alt="Discourse users" src="https://img.shields.io/discourse/users?logo=discourse&server=https%3A%2F%2Fcommunity.neo4j.com">
  </a>
</p>


# Generating the stores

The class that generates the stores is `com.neo4j.fabric.generator.FabricDataGenerator`. The `main` method accepts three arguments:

1. The directory that serves as the store creation area. This is where the store files are kept while the database contents are being created
2. The directory for the log files. This is where the transaction log files will be kept. It is provided as a separate argument because it's recommended that you use a dedidated disk drive for them, if possible, for performance reasons.
3. The directory where the dump files will be kept. After each store is generated, it will be converted to a Neo4j dump file to conserve disk space and will be moved to this directory.

For example, if you want to use the following directory hierarchy for the store generation:

`/mnt/trillion-nodes/database` for the database files

`/mnt/trillion-nodes/tx-logs` for the transaction logs

`/mnt/trillion-nodes/stores` for the dump files

then you would use the following command line

```
java -jar generator.jar /mnt/trillion-nodes/database /mnt/trillion-nodes/tx-logs /mnt/trillion-nodes/stores
```

As provided, the code will generate two databases. The Person shard and a Template database, at the full size of the demo. The parameters for the number of persons, connections and so on, that control the final size, are defined in static fields at the top of the file. Adjust them as required.

The Person shard is just that, the shard containing all the Person nodes and the connections between them. The Template database is a minimized version of the Person shard, that is used as the basis for the Forum shard creation. It contains the Person nodes which need to be the same on all Forum shards so referential integrity is maintained.

By adjusting the `NUMBER_OF_EXPECTED_SHARDS` static field, you control how many Forum shards you want to have created. You can set the value to something larger than `0` and create all shards in one go, locally, and then move them to the Neo4j instances that will function as Fabric shards.

Alternatively, you can do it in separate steps. This allows for parallel Forum shard creation, which can save a lot of time for really big stores. After creating the Person and Template databases and making them accessible (by, for example, uploading them to S3), you can have the code download the Template database (instead of generating it) and use it to create Forum shards. This can be done on multiple machines in parallel and, as long as the same Template database is used, the resulting database will be consistent.

## Parallel store creation

The main orchestration class is `com.neo4j.fabric.generator.amazon.AmazonController`. You can find all the required options and paths as static variables at the top of that file. All of them are required, as they define the account information for controlling the AWS instances.

As provided, the class will start 60 AWS instances, download the data generator and the Template database, create a forum shard, and upload it to S3. The sequence of commands that achieve this are listed in the `execute()` method.

> Keep in mind that AWS doesn't allow a controller to manage more than 100 machines concurrently.

If you look at the commands, they form a script with the steps necessary. After some initial setup, it downloads the `generator.jar` and Template database from your S3 bucket. It goes on to execute the `generator.jar` `main` method, as described above, and then uploads the resulting shard(s) to S3 again. For this to work, you'll need to provide the Template databases to a known S3 path and, of course, prepare the generator code so it can execute without creating first the Template database. To do this, you will need to comment out the appropriate lines in the `FabricDataGenerator.main()` method, recompile the project and upload the resulting jar.

In the end, after everything has been set up, you will compile the project and run it with

```
java -cp "target/*" com.neo4j.fabric.generator.amazon.AmazonController
```
