package com.neo4j.fabric.generator.lockclient;

import org.neo4j.configuration.Config;
import org.neo4j.kernel.impl.locking.Locks;
import org.neo4j.kernel.impl.locking.LocksFactory;
import org.neo4j.time.SystemNanoClock;

public class NoLocksClientFactory implements LocksFactory
{
    @Override
    public Locks newInstance( Config config, SystemNanoClock clock )
    {
        return Locks.NO_LOCKS;
    }

    @Override
    public String getName()
    {
        return "nolocks";
    }

    @Override
    public int getPriority()
    {
        return 1;
    }
}
