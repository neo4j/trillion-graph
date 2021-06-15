package com.neo4j.fabric.generator.data;

import org.neo4j.graphdb.RelationshipType;

public class RelationshipTypes
{
    public static final RelationshipType KNOWS = RelationshipType.withName( "KNOWS" );
    public static final RelationshipType HAS_INTEREST = RelationshipType.withName( "HAS_INTEREST" );
    public static final RelationshipType HAS_TYPE = RelationshipType.withName( "HAS_TYPE" );
    public static final RelationshipType HAS_TAG = RelationshipType.withName( "HAS_TAG" );
    public static final RelationshipType IS_SUBCLASS_OF = RelationshipType.withName( "IS_SUBCLASS_OF" );
    public static final RelationshipType HAS_MEMBER = RelationshipType.withName( "HAS_MEMBER" );
    public static final RelationshipType HAS_MODERATOR = RelationshipType.withName( "HAS_MODERATOR" );
    public static final RelationshipType HAS_CREATOR = RelationshipType.withName( "HAS_CREATOR" );
    public static final RelationshipType LIKES = RelationshipType.withName( "LIKES" );
    public static final RelationshipType CONTAINER_OF = RelationshipType.withName( "CONTAINER_OF" );
    public static final RelationshipType REPLY_OF = RelationshipType.withName( "REPLY_OF" );
}
