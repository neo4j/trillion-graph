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

import neo4j from "neo4j-driver/lib/browser/neo4j-web";
import connectionsConfig from "./connections.json";
export interface Connection {
    name: string;
    shards: number;
    records: Records;
    bolt: string;
    username: string;
    password: string;
}

export type Records = {
    fixedNodes: number;
    nodesPerShard: number;
    fixedRelationships: number;
    relationshipsPerShard: number;
};

export const connections: Connection[] = connectionsConfig;

export const queries = [
    {
      name: "Recent posts of interest",
      description: "Posts tagged with topics that a user is following",
      db: "fabric",
      params: {
        Person: neo4j.int(1346680336),
      },
      query: `call {
        use fabric.persons 
        match (p:Person {id: $Person})-[r:HAS_INTEREST]->(t:Tag) 
        return t.id as tagId
    }
    with distinct tagId
    unwind /*shardsForTag(tagId)*/ [g in [g IN range(tagId-10,tagId+10,2) | g % size(fabric.graphIds())] where g>0]  as gid
    call {
        use fabric.graph(gid)
        with tagId
        match (post:Post)-[:HAS_TAG]->(tag {id:tagId})
        where post.creationDate = date("2021-04-06")
        match (author:Person)<-[:HAS_CREATOR]-(post)
        return author.id as authorId, post as post limit 1
    }
    with authorId, post order by post.creationDate desc limit 10
    call {
        use fabric.persons
        with authorId
        match (author:Person {id:authorId})   
        return author
    }
    return author, post`
    },
    {
        name: "New Topics",
        description: "LDBC Read Interactive / complex / 4",
        db: "fabric",
        params: {
            Duration: neo4j.int(10),
            Date0: new neo4j.types.Date(2021, 4, 10),
            Person: neo4j.int(3214926),
            Tag: "Sandrine_Testud",
        },
        query: `CALL {
            USE fabric.persons
            MATCH (person:Person {id: $Person})-[:KNOWS]-(friend)
            RETURN collect(friend.id) AS personIds
          }
          
          with *, fabric.graphIds()  AS gids
          UNWIND gids AS gid
          CALL {
            USE fabric.graph(gid)
            WITH personIds
            UNWIND personIds AS personId
            MATCH (:Person {id:personId})<-[:HAS_CREATOR]-(post)-[:HAS_TAG]->(tag)
              WHERE $Date0 > post.creationDate
            RETURN DISTINCT tag.id AS invalidTagId
          }
          
          WITH gids, personIds, collect(DISTINCT invalidTagId) AS invalidTagIds
          
          UNWIND gids AS gid
          CALL {
            USE fabric.graph(gid)
            WITH personIds, invalidTagIds
            UNWIND personIds AS personId
            MATCH (:Person {id:personId})<-[:HAS_CREATOR]-(post)-[:HAS_TAG]->(tag)
              WHERE NOT tag.id IN invalidTagIds AND ($Date0+duration({days:$Duration})) > post.creationDate >= $Date0
            RETURN tag.name AS tagName, count(post) AS shardPostCount
          }
          
          RETURN tagName, sum(shardPostCount) AS postCount
            ORDER BY postCount DESC, tagName ASC
            LIMIT 20`,
    },
    {
        name: "Tag co-occurrence",
        description: "LDBC Read Interactive / complex / 6",
        db: "fabric",
        params: { Person: neo4j.int(1346680336), Tag: "Akshay_Kumar" },
        query: `CALL {
            USE fabric.persons
            MATCH (person:Person {id:$Person})-[:KNOWS*1..2]-(friend)
              WHERE NOT person=friend
            WITH DISTINCT friend.id AS fid
            RETURN collect(fid) AS fids
          }
          
          with *, fabric.graphIds()  AS gids
          UNWIND gids AS gid
          CALL {
            USE fabric.graph(gid)
            WITH fids
          
            MATCH (friend:Person) WHERE friend.id IN fids
            MATCH (knownTag:Tag {name:$Tag})
          
            MATCH (friend)<-[:HAS_CREATOR]-(post)-[:HAS_TAG]->(knownTag)
            WITH post, knownTag
            MATCH (post)-[:HAS_TAG]->(tag)
              WHERE NOT tag=knownTag
            WITH tag, count(post) AS postCount
            RETURN tag.name AS tagName, postCount
              ORDER BY postCount DESC, tagName ASC
          }
          RETURN tagName, sum(postCount) AS postCount
            ORDER BY postCount DESC, tagName ASC
            LIMIT 20`,
    },
    {
        name: "Recent likers",
        description: "LDBC Read Interactive / complex / 7",
        db: "fabric",
        params: { Person: neo4j.int(488) },
        query: `WITH fabric.graphIds()  AS gids
        UNWIND gids AS gid
        CALL {
          USE fabric.graph(gid)
          MATCH (:Person {id:$Person})<-[:HAS_CREATOR|HAS_CREATOR]-(message),
                (message)<-[like:LIKES|LIKES]-(liker)
          RETURN liker.id AS likerId, message, like.creationDate AS likeTime
        }
        
        WITH *
            ORDER BY likeTime DESC, message.id ASC
        
        WITH likerId as lid,
             head(collect(message)) AS globalTopMessage,
             head(collect(likeTime)) AS globalTopLikeTime
        
        CALL {
          USE fabric.persons
          WITH lid 
          MATCH (liker:Person {id:lid})
          RETURN liker, not((liker)-[:KNOWS]-({id:$Person})) AS isNew
        }
        RETURN liker.id AS personId,
               liker.firstName AS personFirstName,
               liker.lastName AS personLastName,
               globalTopLikeTime,
               isNew,
               globalTopMessage.id AS messageId,
               coalesce(globalTopMessage.content,globalTopMessage.imageFile) AS messageContent,
               globalTopMessage.creationDate AS messageCreationDate
            ORDER BY globalTopLikeTime DESC, personId ASC
            LIMIT 20`,
    },
    {
        name: "Recent messages by friends or friends of friends",
        description: "LDBC Read Interactive / complex / 9",
        db: "fabric",
        params: {
            Date0: new neo4j.types.Date(2022, 1, 14),
            Person: neo4j.int(1346680336),
        },
        query: `CALL {
            USE fabric.persons
            MATCH (person:Person {id: $Person})-[:KNOWS*1..2]-(friend)
              WHERE NOT person = friend
            WITH DISTINCT friend
            WITH {id: friend.id, firstName: friend.firstName, lastName: friend.lastName} AS person
            RETURN collect(person) AS persons
          }
          
          with *, fabric.graphIds()  AS gids
          UNWIND gids AS gid
          CALL {
            USE fabric.graph(gid)
            WITH persons
            UNWIND persons AS person
          
            MATCH (friend:Person {id: person.id})<-[:HAS_CREATOR|HAS_CREATOR]-(message)
              WHERE message.creationDate < $Date0
            RETURN person, message
              ORDER BY message.creationDate DESC, message.id ASC
              LIMIT 20
          }
          RETURN message.id AS messageId,
                 coalesce(message.content,message.imageFile) AS messageContent,
                 message.creationDate AS messageCreationDate,
                 person.id AS personId,
                 person.firstName AS personFirstName,
                 person.lastName AS personLastName
            ORDER BY messageCreationDate DESC, messageId ASC
            LIMIT 20;`,
    },
];
