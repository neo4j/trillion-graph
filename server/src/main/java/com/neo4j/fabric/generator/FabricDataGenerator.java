package com.neo4j.fabric.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.neo4j.dbms.api.EnterpriseDatabaseManagementServiceBuilder;
import com.neo4j.fabric.generator.data.Tags;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.impl.factory.primitive.LongLists;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseNotFoundException;
import org.neo4j.dbms.archive.CompressionFormat;
import org.neo4j.dbms.archive.Dumper;
import org.neo4j.function.Predicates;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.io.layout.DatabaseLayout;
import org.neo4j.kernel.impl.transaction.log.checkpoint.CheckPointer;
import org.neo4j.kernel.impl.transaction.log.checkpoint.SimpleTriggerInfo;
import org.neo4j.kernel.internal.GraphDatabaseAPI;

import static com.neo4j.configuration.MetricsSettings.metrics_enabled;
import static com.neo4j.fabric.generator.data.Labels.COMMENT;
import static com.neo4j.fabric.generator.data.Labels.FORUM;
import static com.neo4j.fabric.generator.data.Labels.PERSON;
import static com.neo4j.fabric.generator.data.Labels.POST;
import static com.neo4j.fabric.generator.data.Labels.TAG;
import static com.neo4j.fabric.generator.data.PersonData.randomFirstName;
import static com.neo4j.fabric.generator.data.PersonData.randomLastName;
import static com.neo4j.fabric.generator.data.Properties.CONTENT;
import static com.neo4j.fabric.generator.data.Properties.CREATION_DATE;
import static com.neo4j.fabric.generator.data.Properties.FIRST_NAME;
import static com.neo4j.fabric.generator.data.Properties.ID;
import static com.neo4j.fabric.generator.data.Properties.IMAGE_FILE;
import static com.neo4j.fabric.generator.data.Properties.LAST_NAME;
import static com.neo4j.fabric.generator.data.Properties.TAG_NAME;
import static com.neo4j.fabric.generator.data.RelationshipTypes.CONTAINER_OF;
import static com.neo4j.fabric.generator.data.RelationshipTypes.HAS_CREATOR;
import static com.neo4j.fabric.generator.data.RelationshipTypes.HAS_INTEREST;
import static com.neo4j.fabric.generator.data.RelationshipTypes.HAS_TAG;
import static com.neo4j.fabric.generator.data.RelationshipTypes.KNOWS;
import static com.neo4j.fabric.generator.data.RelationshipTypes.LIKES;
import static com.neo4j.fabric.generator.data.RelationshipTypes.REPLY_OF;
import static java.time.Duration.ofDays;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.concurrent.locks.LockSupport.parkNanos;
import static org.neo4j.configuration.GraphDatabaseInternalSettings.lock_manager;
import static org.neo4j.configuration.GraphDatabaseInternalSettings.vm_pause_monitor_measurement_duration;
import static org.neo4j.configuration.GraphDatabaseSettings.TransactionStateMemoryAllocation.ON_HEAP;
import static org.neo4j.configuration.GraphDatabaseSettings.check_point_iops_limit;
import static org.neo4j.configuration.GraphDatabaseSettings.fail_on_missing_files;
import static org.neo4j.configuration.GraphDatabaseSettings.keep_logical_logs;
import static org.neo4j.configuration.GraphDatabaseSettings.pagecache_memory;
import static org.neo4j.configuration.GraphDatabaseSettings.pagecache_warmup_enabled;
import static org.neo4j.configuration.GraphDatabaseSettings.preallocate_logical_logs;
import static org.neo4j.configuration.GraphDatabaseSettings.transaction_logs_root_path;
import static org.neo4j.configuration.GraphDatabaseSettings.tx_state_memory_allocation;

public class FabricDataGenerator
{
    /*
     * These are the main parameters for the store model.
     */
    private static final long NUMBER_OF_PERSONS = 3_000_000_000L;
    private static final int MAX_KNOW_PERSON = 10;
    private static final int BATCH_SIZE = 10_000;

    public static final int NUMBER_OF_TAGS = 1000;
    public static final int SHARD_FORUMS = 10_000;

    public static final int MIN_POSTS_PER_FORUM = 100;
    public static final int MAX_POSTS_PER_FORUM = 800;
    public static final int MIN_COMMENTS_PER_POST = 0;
    public static final int MAX_COMMENTS_PER_POST = 80;

    public static final int NUMBER_OF_EXPECTED_SHARDS = 0;

    private static final String TEMPLATE_DB = "template";
    private static final String PERSONS_DB_NAME = "persons";
    private static final String FORUM_DB_PREFIX = "forum";

    public static void main( String[] args ) throws InterruptedException, IOException
    {
        Path databaseRoot = Path.of( args[0] );
        Path logsDirectory = Path.of( args[1] );
        Path dumpsRoot = Path.of( args[2] );
        FileUtils.deleteDirectory( logsDirectory );
        System.out.println( "Start generating fabric stores at: " + databaseRoot );

        var managementService =
                new EnterpriseDatabaseManagementServiceBuilder( databaseRoot )
                        .setConfig( tx_state_memory_allocation, ON_HEAP )
                        .setConfig( transaction_logs_root_path, logsDirectory )
                        .setConfig( metrics_enabled, false )
                        .setConfig( pagecache_memory, "30g" )
                        .setConfig( lock_manager, "nolocks" )
                        .setConfig( preallocate_logical_logs, false )
                        .setConfig( pagecache_warmup_enabled, false )
                        .setConfig( vm_pause_monitor_measurement_duration, ofDays( 10 ) )
                        .setConfig( keep_logical_logs, "keep_none" )
                        .setConfig( fail_on_missing_files, false )
                        .setConfig( check_point_iops_limit, -1 )
                        .build();

        /*
         * Here you control which databases get generated. In general, you would first create the person and template
         * databases, by setting the NUMBER_OF_EXPECTED_SHARDS to 0. After uploading these to an S3 bucket, you'd
         * comment the following two lines out and set the NUMBER_OF_EXPECTED_SHARDS to however many shards you want to
         * create and optionally orchestrate so this code runs on multiple machines in parallel.
         */
        generatePersonDatabase( PERSONS_DB_NAME, getDatabase( managementService, PERSONS_DB_NAME ), StopWatch.createStarted() );
        generateTemplateDb( managementService, StopWatch.createStarted() );

        // This line is here so that even if the template db generation is skipped, everything still works.
        var templateLayout = getTemplateDb( managementService );


        var populators = Executors.newCachedThreadPool();
        try
        {
            CountDownLatch shardLatch = new CountDownLatch( NUMBER_OF_EXPECTED_SHARDS );
            for ( int i = 0; i < NUMBER_OF_EXPECTED_SHARDS; i++ )
            {
                populators.submit( () ->
                {
                    try
                    {
                        StopWatch stopWatch = StopWatch.createStarted();
                        generateForumDb( FORUM_DB_PREFIX, managementService, stopWatch, UUID.randomUUID().toString(), templateLayout );
                    }
                    catch ( Exception e )
                    {
                        e.printStackTrace();
                        throw new RuntimeException( e );
                    }
                    finally
                    {
                        shardLatch.countDown();
                    }
                } );
            }
            shardLatch.await();
        }
        finally
        {
            populators.shutdownNow();
        }
        System.out.println("Generation of the stores completed.");

        List<String> forums = managementService.listDatabases().stream().filter( name -> name.startsWith( FORUM_DB_PREFIX ) ).collect( Collectors.toList() );

        managementService.shutdown();

        dumpDatabased( databaseRoot, logsDirectory, dumpsRoot, forums );
    }

    private static void dumpDatabased( Path databaseRoot, Path logsDirectory, Path dumpsRoot, List<String> forums ) throws IOException
    {
        Dumper dumper = new Dumper( System.out );
        Files.createDirectories(dumpsRoot);
        for ( String forum : forums )
        {
            System.out.println("Dump " + forum);
            dumper.dump( databaseRoot.resolve( "data" ).resolve( "databases" ).resolve( forum ), logsDirectory.resolve( forum ),
                    dumpsRoot.resolve( forum + ".dump" ), CompressionFormat.ZSTD,
                    Predicates.alwaysFalse() );
        }
        System.out.println("Dumping completed.");
    }

    private static void generatePersonDatabase( String personsDbName, GraphDatabaseService personDatabase, StopWatch stopWatch )
    {
        System.out.println( "Start generating " + personsDbName + " database." );
        generatePersons( personDatabase, false );
        printPhaseTime( stopWatch, "People for %s generated in %s.", personsDbName );
        var tagNodeIds = generateTags( personDatabase );
        printPhaseTime( stopWatch, "Tags for %s generated in %s.", personsDbName );

        linkPeople( personDatabase, tagNodeIds );
        printPhaseTime( stopWatch, "People for %s linked in %s." , personsDbName );

        createBaseConstraints( personDatabase );
        printPhaseTime( stopWatch, "Constraints for %s created in %s." , personsDbName );

        System.out.println("Main " + personsDbName + " database generated.");
    }

    private static void generateForumDb( String baseName, DatabaseManagementService managementService, StopWatch stopWatch, String suffix,
            DatabaseLayout templateLayout ) throws IOException
    {
        var forumDbName = baseName + suffix;
        System.out.println( "========== Start generating " + forumDbName + " =================");
        copyPersonTemplate( templateLayout, forumDbName );
        printPhaseTime( stopWatch, "Template db copied in: %s.", forumDbName );

        GraphDatabaseService forumDb = getDatabase( managementService, forumDbName );
        var forumTagNodeIds = generateTags( forumDb );
        printPhaseTime( stopWatch, "Tags for %s generated in: %s.", forumDbName );
        generateForums( forumDb, forumTagNodeIds );
        printPhaseTime( stopWatch, "Forums for %s generated in: %s.", forumDbName );
        generatePosts( forumDb, forumTagNodeIds );
        printPhaseTime( stopWatch, "Posts for %s generated in: %s.", forumDbName );
        generateComments( forumDb, forumTagNodeIds );
        printPhaseTime( stopWatch, "Comments for %s generated in: %s.", forumDbName );

        System.out.println( "Start index population for " + forumDbName );
        createBaseConstraints( forumDb );
        createForumSpecificIndexes( forumDb );
        printPhaseTime( stopWatch, "Index for %s created in %s." , forumDbName );
    }

    private static void copyPersonTemplate( DatabaseLayout templateLayout, String forumDbName ) throws IOException
    {
        Path databaseTarget = templateLayout.databaseDirectory().resolveSibling( forumDbName );
        Path logsTarget = templateLayout.getTransactionLogsDirectory().resolveSibling( forumDbName );
        FileUtils.deleteDirectory( databaseTarget );
        FileUtils.deleteDirectory( logsTarget );
        FileUtils.copyDirectory( templateLayout.databaseDirectory(), databaseTarget );
        FileUtils.copyDirectory( templateLayout.getTransactionLogsDirectory(), logsTarget );
    }

    private static DatabaseLayout generateTemplateDb( DatabaseManagementService managementService, StopWatch stopWatch ) throws IOException
    {
        var templateDb = getDatabase( managementService, TEMPLATE_DB );
        var databaseLayout = ((GraphDatabaseAPI) templateDb).databaseLayout();
        var checkPointer = ((GraphDatabaseAPI) templateDb).getDependencyResolver().resolveDependency( CheckPointer.class );

        generatePersons( templateDb, true );
        printPhaseTime( stopWatch, "People for %s generated in: %s.", TEMPLATE_DB );
        checkPointer.forceCheckPoint( new SimpleTriggerInfo( "Force flush" ) );
        managementService.shutdownDatabase( TEMPLATE_DB );

        return databaseLayout;
    }

    private static DatabaseLayout getTemplateDb( DatabaseManagementService managementService ) throws IOException
    {
        var templateDb = getDatabase( managementService, TEMPLATE_DB );
        return ((GraphDatabaseAPI) templateDb).databaseLayout();
    }

    private static void createForumSpecificIndexes( GraphDatabaseService forumDb )
    {
        try ( Transaction transaction = forumDb.beginTx() )
        {
            transaction.schema().indexFor( POST ).on( CREATION_DATE ).create();
            transaction.schema().indexFor( TAG ).on( TAG_NAME ).create();
            transaction.commit();
        }
        try ( Transaction transaction = forumDb.beginTx() )
        {
            transaction.schema().awaitIndexesOnline( 1, TimeUnit.DAYS );
        }
    }

    private static void createBaseConstraints( GraphDatabaseService db )
    {
        try ( Transaction transaction = db.beginTx() )
        {
            transaction.schema().constraintFor( PERSON ).assertPropertyIsUnique( ID ).create();
            transaction.commit();
        }
        try ( Transaction transaction = db.beginTx() )
        {
            transaction.schema().constraintFor( TAG ).assertPropertyIsUnique( ID ).create();
            transaction.commit();
        }
    }

    private static void printPhaseTime( StopWatch stopWatch, String messageTemplate, String databaseName )
    {
        System.out.printf( "Db: %s(Thread Id: %d) : " + messageTemplate + "%n", databaseName, Thread.currentThread().getId(), databaseName, stopWatch.formatTime() );
        stopWatch.stop();
        stopWatch.reset();
        stopWatch.start();
    }

    private static LongList generateTags( GraphDatabaseService database )
    {
        int tagNameIndex = 0;
        var tags = LongLists.mutable.empty();
        var random = ThreadLocalRandom.current();
        try ( Transaction transaction = database.beginTx() )
        {
            for ( int i = 0; i < NUMBER_OF_TAGS; i++ )
            {
                var tagNode = transaction.createNode( TAG );
                tagNode.setProperty( TAG_NAME, Tags.TAGS[tagNameIndex] );
                tagNode.setProperty( ID, i );
                tagNameIndex += random.nextInt( 1, 3 );
                tags.add( tagNode.getId() );
            }
            transaction.commit();
        }
        return tags;
    }

    private static LongList collectTags( GraphDatabaseService database )
    {
        var tags = LongLists.mutable.empty();
        try ( Transaction transaction = database.beginTx() )
        {
            try ( ResourceIterator<Node> nodes = transaction.findNodes( TAG ) )
            {
                while ( nodes.hasNext() )
                {
                    tags.add( nodes.next().getId() );
                }
            }
        }
        return tags;
    }

    private static void generateComments( GraphDatabaseService database, LongList forumTagNodeIds )
    {
        var random = ThreadLocalRandom.current();
        int tagsSize = forumTagNodeIds.size();
        int commentId = 0;
        try ( var postTx = database.beginTx() )
        {
            try ( var posts = postTx.findNodes( POST ) )
            {
                while ( posts.hasNext() )
                {
                    commentId = generateComments( database, random, tagsSize, commentId, forumTagNodeIds, posts.next().getId() );
                }
            }
            postTx.commit();
        }
    }

    private static int generateComments( GraphDatabaseService database, ThreadLocalRandom random, int tagsSize, int commentId, LongList forumTagNodeIds,
            long postNodeId )
    {
        try ( Transaction commentsTx = database.beginTx() )
        {
            Node postNode = commentsTx.getNodeById( postNodeId );
            int comments = random.nextInt( MIN_COMMENTS_PER_POST, MAX_COMMENTS_PER_POST );
            long personBaseId = random.nextLong( 1, NUMBER_OF_PERSONS - comments );
            var commentNodes = new ArrayList<Node>();
            for ( int j = 0; j < comments; j++ )
            {
                Node commentNode = commentsTx.createNode( COMMENT );
                Node creator = getPerson( personBaseId + j, commentsTx );
                commentNode.createRelationshipTo( creator, HAS_CREATOR );

                commentNode.setProperty( ID, commentId++ );
                commentNode.setProperty( CONTENT, "That's amazing!" );
                commentNode.setProperty( IMAGE_FILE, "commentImage" + commentId + ".png" );

                commentNodes.add( commentNode );
                var commentsTag = commentsTx.getNodeById( forumTagNodeIds.get( random.nextInt( tagsSize ) ) );
                commentNode.createRelationshipTo( commentsTag, HAS_TAG );

                for ( int k = 0; k < random.nextInt( 5 ); k++ )
                {
                    Node liker = getPerson( creator.getId() - k, commentsTx );
                    if ( liker != null )
                    {
                        Relationship likeRelationship = liker.createRelationshipTo( commentNode, LIKES );
                        likeRelationship.setProperty( CREATION_DATE, LocalDate.now().minusDays( random.nextInt( 10 ) ) );
                    }
                }

                commentNode.createRelationshipTo( postNode, REPLY_OF );
                if ( random.nextBoolean() )
                {
                    Node replyToNode = commentNodes.get( random.nextInt( commentNodes.size() ) );
                    if ( replyToNode != commentNode )
                    {
                        commentNode.createRelationshipTo( replyToNode, REPLY_OF );
                    }
                }
                commentNode.setProperty( CREATION_DATE, LocalDate.now().minusDays( random.nextInt( 10 ) ) );
                reportMillionProgress( "Generated comments for db: " + database.databaseName(), commentId );
            }
            commentsTx.commit();
        }
        return commentId;
    }

    private static void generatePosts( GraphDatabaseService forumDb, LongList forumTagNodeIds )
    {
        var random = ThreadLocalRandom.current();
        int tagsSize = forumTagNodeIds.size();
        int postId = 0;
        try ( var forumTransaction = forumDb.beginTx() )
        {
            try ( var forums = forumTransaction.findNodes( FORUM ) )
            {
                while ( forums.hasNext() )
                {
                    try ( Transaction postTransaction = forumDb.beginTx() )
                    {
                        Node currentForum = forums.next();
                        Node forumNode = postTransaction.getNodeById( currentForum.getId() );
                        int posts = random.nextInt( MIN_POSTS_PER_FORUM, MAX_POSTS_PER_FORUM );
                        for ( int j = 0; j < posts; j++ )
                        {
                            Node postNode = postTransaction.createNode( POST );
                            Node creator = getPerson( random, postTransaction );
                            postNode.createRelationshipTo( creator, HAS_CREATOR );

                            postNode.setProperty( ID, postId++ );
                            postNode.setProperty( CONTENT, "Graphs are everywhere!" );
                            postNode.setProperty( IMAGE_FILE, "postImage" + postId + ".png" );

                            for ( int k = 0; k < random.nextInt( 1, 3 ); k++ )
                            {
                                Node postTag = postTransaction.getNodeById( forumTagNodeIds.get( random.nextInt( tagsSize ) ) );
                                postNode.createRelationshipTo( postTag, HAS_TAG );
                            }

                            postNode.setProperty( CREATION_DATE, LocalDate.now().minusDays( random.nextInt(100) ) );

                            for ( int k = 0; k < random.nextInt( 5 ); k++ )
                            {
                                Node liker = getPerson( creator.getId() + k, postTransaction );
                                if ( liker != null )
                                {
                                    Relationship likeRelationship = liker.createRelationshipTo( postNode, LIKES );
                                    likeRelationship.setProperty( CREATION_DATE, LocalDate.now().minusDays( random.nextInt( 10 ) ) );
                                }
                            }

                            forumNode.createRelationshipTo( postNode, CONTAINER_OF );
                            reportMillionProgress( "Posts generated for db " + forumDb.databaseName(), postId );
                        }
                        postTransaction.commit();
                    }
                }
            }
            forumTransaction.commit();
        }
    }

    private static Node getPerson( ThreadLocalRandom random, Transaction transaction )
    {
        do
        {
            var node = transaction.getNodeById( random.nextLong( 1, NUMBER_OF_PERSONS ) );
            if ( node.hasLabel( PERSON ) )
            {
                return node;
            }
        }
        while ( true );
    }

    private static Node getPerson( long id, Transaction transaction )
    {
        try
        {
            var node = transaction.getNodeById( id );
            if ( node.hasLabel( PERSON ) )
            {
                return node;
            }
            return null;
        } catch ( NotFoundException e ) {
            return null;
        }
    }

    private static void generateForums( GraphDatabaseService forumDb, LongList forumTagNodeIds )
    {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int numberOfTags = forumTagNodeIds.size();
        try ( var transaction = forumDb.beginTx() )
        {
            for ( int i = 0; i < SHARD_FORUMS; i++ )
            {
                var forumNode = transaction.createNode( FORUM );
                var tag = transaction.getNodeById( forumTagNodeIds.get( random.nextInt( numberOfTags ) ) );
                forumNode.createRelationshipTo( tag, HAS_TAG );
            }
            transaction.commit();
        }
    }

    private static void linkPeople( GraphDatabaseService personDatabase, LongList tagNodeIds )
    {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int numberOfTags = tagNodeIds.size();
        try ( var personIterationTransaction = personDatabase.beginTx() )
        {
            try ( var personNodes = personIterationTransaction.findNodes( PERSON ) )
            {
                long linkedPeople = 0;
                while ( personNodes.hasNext() )
                {
                    try ( Transaction innerTx = personDatabase.beginTx() )
                    {
                        for ( int i = 0; i < BATCH_SIZE && personNodes.hasNext(); i++ )
                        {
                            Node person = innerTx.getNodeById( personNodes.next().getId() );
                            person.createRelationshipTo( innerTx.getNodeById( tagNodeIds.get( random.nextInt( numberOfTags ) ) ), HAS_INTEREST );
                            long maxKnownId = person.getId() + random.nextInt( MAX_KNOW_PERSON );
                            for ( long knownId = person.getId() + 1; knownId <= maxKnownId; knownId++ )
                            {
                                try
                                {
                                    var knownPerson = innerTx.getNodeById( knownId );
                                    if ( knownPerson.hasLabel( PERSON ) )
                                    {
                                        person.createRelationshipTo( knownPerson, KNOWS );
                                    }
                                }
                                catch ( NotFoundException e )
                                {
                                    // ignore
                                }
                            }
                            linkedPeople++;
                        }
                        innerTx.commit();
                    }
                    reportMillionProgress( "Linked people", linkedPeople );
                }
            }
            personIterationTransaction.commit();
        }
    }

    private static void generatePersons( GraphDatabaseService database, boolean proxy )
    {
        int personId = 0;
        for ( long i = 0; i <= NUMBER_OF_PERSONS; i += BATCH_SIZE )
        {
            try ( Transaction transaction = database.beginTx() )
            {
                for ( int j = 0; j < BATCH_SIZE; j++ )
                {
                    var personNode = transaction.createNode( PERSON );
                    int idProperty = personId++;
                    personNode.setProperty( ID, idProperty );
                    if ( !proxy )
                    {
                        personNode.setProperty( FIRST_NAME, randomFirstName() );
                        personNode.setProperty( LAST_NAME, randomLastName() );
                    }
                }
                transaction.commit();
                reportMillionProgress( "Generated people", i );
            }
        }
    }

    private static void reportMillionProgress( String baseMessage, long i )
    {
        if ( (i > 0) && ((i % 1000000) == 0) )
        {
            System.out.println( baseMessage + ": " + i );
        }
    }

    private static GraphDatabaseService getDatabase( DatabaseManagementService managementService, String dbName )
    {
        if ( managementService.listDatabases().contains( dbName ) )
        {
            return managementService.database( dbName );
        }
        managementService.createDatabase( dbName );
        while ( true )
        {
            try
            {
                var db = managementService.database( dbName );
                while ( !db.isAvailable( 0 ) )
                {
                    parkNanos( SECONDS.toNanos( 1 ) );
                }
                return db;
            }
            catch ( DatabaseNotFoundException e )
            {
                // ignore
                parkNanos( SECONDS.toNanos( 1 ) );
            }
        }
    }
}
