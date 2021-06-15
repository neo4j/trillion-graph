call {
    use fabric.persons 
    match (p:Person {id:1346680336})-[r:HAS_INTEREST]->(t:Tag) 
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
return author, post