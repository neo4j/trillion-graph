CALL {
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
            LIMIT 20