package com.neo4j.fabric.generator.data;

import org.neo4j.graphdb.Label;

public class Labels
{
    public static final Label PERSON = Label.label( "Person" );
    public static final Label TAG = Label.label( "Tag" );
    public static final Label FORUM = Label.label( "Forum" );
    public static final Label POST = Label.label( "Post" );
    public static final Label COMMENT = Label.label( "Comment" );
}
