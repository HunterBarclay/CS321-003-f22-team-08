# The SUPER UBER DUBER AWESOME team

# Team Members

Last Name       | First Name      | GitHub User Name
--------------- | --------------- | --------------------
Barclay         | Hunter          | KyroVibe
Pierce          | Tyler           | tylerpierce683
Pfeil           | Joshua          | joshuapfeil

# Test Results
How many of the dumpfiles matched (using the check-dumpfiles.sh script)?
All of them

How many of the query files results matched (using the check-queries.sh script)?
All of them

# Cache Performance Results
For the test data `test5.gbk`, how much did a Cache of size 100 improve your performance compared to no cache.

It speed it up by more than a factor of 2.

For the test data `test5.gbk`, how much did a Cache of size 500 improve your performance compared to no cache? 

A tiny bit slower than a 100 size cache.

For the test data `test5.gbk`, how much did a Cache of size 5000 improve your performance compared to no cache?

I actually just stopped this one, because it was over 2 minutes and that was considerably slower than with no cache. I suspect the added looping through thousands of elements each retrieval of a node hindered quite a bit.


# AWS Notes
Brief reflection on your experience with running your code on AWS.

# Reflection

Provide a reflection by each of the team member (in a separate subsection)

## Reflection (Team member name: Hunter Barclay)
This project was pretty fun. I enjoy actually using a build system for a school project instead of juggling around a ton of class files. The utility classes were almost satisfying and the database integration was really insightful as I've never really managed database stuff on my own before. All in all, 10 out of 10, would do again.

## Reflection (Team member name: Tyler Pierce)
I thought that this project was overall a lot to handle. It was my first time really working on a computer science project in a group, so it was difficult to figure out the dynamic with that. It was also difficult at times working with git because I didn't remember specific commands and how to do things with git and making sure things are merging well. It was also my first time using branches in git which was interesting and gave me good experience for the future. I did think it was hard to see the overall idea of the project for a long time. Once I eventually saw the idea of the project, things became easier. There were lots of bugs that I had made which was frustrating. It was also hard working in a group because we had to wait for other things to be done before we could effectively test our pieces. Overall though, I think it didn't go too bad and I'm happy with the results.

## Reflection (Team member name: Joshua Pfeil)
I think that this project was kind of fun in a way. I have always enjoyed working with large datasets and complex algorithms so it was great that we were able to do this in this project. Unlike other data structures, Btrees have to utilize the disk due to the massive data sizes it works with. I really liked that we used a real world scenario of geneBank files, espicially since my Sister is a biology major so it was an interesting connection. Working with a team in CS and fully untilizing github was a bit new so it was interesting to do. Since teams are often required in the real world, it is good to gain programming experience from such a realistic scenario. As for the code, the main hurdle I had to get through was making insert working, where it found many ways to delete, duplicate, and overall mess up existing keys during a split. Fortunately it was finally finished, though i was upset that it did not make it to the final version.

# Additional Notes
We went with a "one node per file" approach to adjust for a generic storage type.

