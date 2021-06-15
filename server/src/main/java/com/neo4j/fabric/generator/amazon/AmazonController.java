package com.neo4j.fabric.generator.amazon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.BlockDeviceMapping;
import software.amazon.awssdk.services.ec2.model.DescribeInstanceStatusRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.EbsBlockDevice;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.StartInstancesRequest;
import software.amazon.awssdk.services.ec2.model.TerminateInstancesRequest;
import software.amazon.awssdk.services.ec2.model.VolumeType;

public class AmazonController
{
    /*
     * Sample configuration values and credential paths. Change them as required.
     */
    private static final int NUMBER_OF_INSTANCES = 60;
    // Local credentials to connect with to AWS
    private static final String PEM_IDENTITY = "/home/user/.ssh/user-amazon.pem";
    public static final String AWS_CREDENTIALS = "/home/user/.aws/credentials";
    public static final String AWS_CONFIG = "/home/user/.aws/config";
    // AWS Instance type
    public static final String IMAGE_ID = "ami-06868ad5a3642e4d7";
    // AWS account specific information
    public static final String KEY_NAME = "<keyname>";
    public static final String SECURITY_GROUP = "<security group>";
    // S3 paths for database storage and the location of the generator code
    public static final String GENERATOR_JAR_PATH = "s3://path/to/generator.jar";
    public static final String TEMPLATE_DATABASE_PATH = "s3://path/to/template.tar.zst";
    public static final String FORUM_DATABASE_STORAGE_PATH = "s3://path/to/big-database/";
    /*
     * The execute() method also contains hardwired paths for the ubuntu user directory as the location
     * where the generator will store its data. The provided should work just fine, but they can be changed
     * in the commands themselves if required.
     */

    public static void main( String[] args ) throws ExecutionException, InterruptedException
    {
        Region region = Region.EU_WEST_1;
        try ( Ec2Client ec2Client = Ec2Client.builder().region( region ).build() )
        {
            System.out.println( "Connected to EC2." );
            AmazonController amazonController = new AmazonController();
            var instances = amazonController.createInstance( ec2Client, IMAGE_ID );
            var machines = amazonController.startInstance( ec2Client, instances );
            amazonController.execute( machines );

            amazonController.terminateInstance( ec2Client, machines );
            System.out.println( "Generation completed." );
        }
    }

    public void execute( Collection<AmazonMachine> instances ) throws ExecutionException, InterruptedException
    {
        ExecutorService executors = Executors.newFixedThreadPool( instances.size() );
        try
        {
            var tasks = new ArrayList<Future<?>>();
            for ( AmazonMachine instance : instances )
            {
                tasks.add( executors.submit( () ->
                {
                    try
                    {
                        JSch jSch = new JSch();
                        jSch.addIdentity( PEM_IDENTITY );
                        var session = jSch.getSession( "ubuntu", instance.getPublicDnsName() );
                        session.setConfig( "StrictHostKeyChecking", "no" );
                        session.connect();
                        try
                        {
                            var commands = List.of( "sudo apt-get update",
                                    "sudo apt install awscli --yes",
                                    "sudo apt install zstd --yes",
                                    "sudo apt install openjdk-11-jdk-headless --yes",
                                    "mkdir -p /home/ubuntu/.aws",
                                    "mkdir -p /home/ubuntu/data",
                                    "mkdir -p /home/ubuntu/data/fabric/data/databases",
                                    "echo \"" + Files.readString( Path.of( AWS_CREDENTIALS ) ) + "\" > /home/ubuntu/.aws/credentials",
                                    "echo \"" + Files.readString( Path.of( AWS_CONFIG ) ) + "\" > /home/ubuntu/.aws/config",
                                    "aws s3 cp " + GENERATOR_JAR_PATH +" /home/ubuntu/generator.jar",
                                    "aws s3 cp " + TEMPLATE_DATABASE_PATH + " /home/ubuntu/data/template.tar.zst",
                                    "tar -I 'zstd -T0' -xf /home/ubuntu/data/template.tar.zst -C /home/ubuntu/data/fabric/data/databases/",
                                    "java -Xmx16g -Xms16g -XX:+UseParallelGC -jar /home/ubuntu/generator.jar /home/ubuntu/data/fabric /tmp/logs /home/ubuntu/data/dumps",
                                    "aws s3 cp /home/ubuntu/data/dumps " + FORUM_DATABASE_STORAGE_PATH + " --recursive" );
                            for ( var command : commands )
                            {
                                executeCommand( session, command );
                            }
                        }
                        finally
                        {
                            session.disconnect();
                        }
                    }
                    catch ( Exception e )
                    {
                        e.printStackTrace();
                        throw new RuntimeException( e );
                    }
                } ) );
            }
            for ( Future<?> task : tasks )
            {
                task.get();
            }
        }
        finally
        {
            executors.shutdown();
        }
    }

    private void executeCommand( com.jcraft.jsch.Session session, String command ) throws JSchException, IOException
    {
        ChannelExec exec = (ChannelExec) session.openChannel( "exec" );
        try
        {
            exec.setCommand( command );
            exec.setErrStream( System.err, true );
            exec.setInputStream( System.in, true );
            exec.setOutputStream( System.out, true );
            exec.connect();
            wait( session, exec );
            int exitStatus = exec.getExitStatus();
            System.out.println( "Status: " + exitStatus );
            if ( exitStatus != 0 )
            {
                throw new RuntimeException( "Command: " + command + " failed with exist status: " + exec );
            }
        }
        finally
        {
            exec.disconnect();
        }
    }

    private void wait( com.jcraft.jsch.Session session, ChannelExec exec ) throws IOException
    {
        while ( !exec.isClosed() )
        {
            try
            {
                session.sendKeepAliveMsg();
            }
            catch ( final Exception ex )
            {
                throw new IOException( ex );
            }
            try
            {
                TimeUnit.SECONDS.sleep( 1L );
            }
            catch ( final InterruptedException ex )
            {
                Thread.currentThread().interrupt();
                throw new IOException( ex );
            }
        }
    }

    public Collection<String> createInstance( Ec2Client ec2Client, String amiId )
    {
        RunInstancesRequest runRequest =
                RunInstancesRequest.builder().imageId( amiId ).keyName( KEY_NAME ).securityGroups( SECURITY_GROUP ).instanceType(
                        InstanceType.M5_4_XLARGE ).maxCount( NUMBER_OF_INSTANCES ).minCount( NUMBER_OF_INSTANCES ).blockDeviceMappings(
                        BlockDeviceMapping.builder().deviceName( "/dev/sda1" ).ebs(
                                EbsBlockDevice.builder().volumeType( VolumeType.GP3 ).volumeSize( 2048 ).build() ).build() ).build();

        var runResponse = ec2Client.runInstances( runRequest );
        List<Instance> instances = runResponse.instances();
        return instances.stream().map( Instance::instanceId ).collect( Collectors.toList() );
    }

    public Collection<AmazonMachine> startInstance( Ec2Client ec2Client, Collection<String> instanceIds )
    {
        System.out.println( "Starting " + instanceIds );
        StartInstancesRequest request = StartInstancesRequest.builder().instanceIds( instanceIds ).build();
        ec2Client.startInstances( request );

        DescribeInstanceStatusRequest statusRequest = DescribeInstanceStatusRequest.builder().instanceIds( instanceIds ).build();
        ec2Client.waiter().waitUntilInstanceStatusOk( statusRequest );

        System.out.printf( "Successfully started instances %s.%n", instanceIds );

        var machines = new ArrayList<AmazonMachine>( instanceIds.size() );
        var describeRequest = DescribeInstancesRequest.builder().instanceIds( instanceIds ).build();
        DescribeInstancesResponse instancesResponse = ec2Client.describeInstances( describeRequest );
        for ( Reservation reservation : instancesResponse.reservations() )
        {
            for ( Instance instance : reservation.instances() )
            {
                System.out.println( "Instance: " + instance.instanceId() + " created." );
                machines.add( new AmazonMachine( instance ) );
            }
        }
        return machines;
    }

    public void terminateInstance( Ec2Client ec2Client, Collection<AmazonMachine> machines )
    {
        var instanceIds = getMachineIds( machines );
        var request = TerminateInstancesRequest.builder().instanceIds( instanceIds ).build();

        ec2Client.terminateInstances( request );

        System.out.printf( "Successfully terminated instances %s.%n", instanceIds );
    }

    private static Collection<String> getMachineIds( Collection<AmazonMachine> machines )
    {
        return machines.stream().map( AmazonMachine::getInstanceId ).collect( Collectors.toList() );
    }
}
