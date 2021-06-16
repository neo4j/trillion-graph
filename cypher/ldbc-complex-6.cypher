CALL {
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
            LIMIT 20