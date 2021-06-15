CALL {
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
            LIMIT 20;