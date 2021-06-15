package com.neo4j.fabric.generator.amazon;

import software.amazon.awssdk.services.ec2.model.Instance;

public class AmazonMachine
{
    private final String instanceId;
    private final String publicDnsName;

    public AmazonMachine( Instance instance )
    {
        this( instance.instanceId(), instance.publicDnsName() );
    }

    public AmazonMachine( String instanceId, String publicDnsName )
    {
        this.instanceId = instanceId;
        this.publicDnsName = publicDnsName;
    }

    public String getInstanceId()
    {
        return instanceId;
    }

    public String getPublicDnsName()
    {
        return publicDnsName;
    }

    @Override
    public String toString()
    {
        return "AmazonMachine{" + "instanceId='" + instanceId + '\'' + ", publicDnsName='" + publicDnsName + '\'' + '}';
    }
}
