WITH fabric.graphIds()  AS gids
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
            LIMIT 20