# Power-Grid-Maintenance
Challenge at LeetCode.com. Tags: Union Find, Graph, Heap, Priority Queue, Hash Set, Ordered Set.

---------------------------------------------------------------------------------------------------------------------------------------------------------------

The presented solution is in three variants, depending on the way the information about operational stations is processed.
 
**Variant 1**

The most up to date information about the operational stations is stored in a Hash Set. To quickly access the operational station with the smallest ID (if the station taken for maintenance is off grid), a Priority Queue also stores the operational stations ID. Each closed grid has its own Hash Set and Priority Queue.

However, the information stored in the Priority Queue is not necessarily up to date. It is updated lazily. When the operational station with the smallest ID is needed and the station at the top the Priority Queue is also contained in the Hash Set, then the ID of this station is returned. Otherwise, the Priority Queue pops up stations until the station at the top is also contained in the Hash Set. If there are no operational stations left, the answer is -1.

Variant 1 is implemented in Java, JavaScript, TypeScript, C++, C#, Kotlin and Golang.
 
**Variant 2**

Similar to Variant 1, but in this case, the most up to date information about the operational stations is stored in a Boolean Array instead of a Hash Set. Here the Boolean Array is implemented for all the stations, i.e. there is only one Boolean Array that encompasses all the closed grids, as opposed to Variant 1, where each closed grid has its own Hash Set. And similar to Variant 1, each closed grid has its own Priority Queue.

Variant 1 can also be implemented with only one Hash Set that encompasses all closed grids, which could slightly improve its efficiency. On the other hand, if Variant 2 is implemented like Variant 1, i.e. there is a Boolean Array for each closed grid, this will significantly decrease its efficiency, to the degree that such an implementation will exceed the memory limits given by Leetcode for the solution of this problem.

Variant 2 is implemented in Java, JavaScript, TypeScript, C++, C#, Kotlin and Golang.

**Variant 3**

The most up to date information about the operational stations is stored in an Ordered Set. When the operational station with the smallest ID is needed, the ID of the station at the top of the Ordered Set is returned. And again, if there are no operational stations left, the answer is -1. Each closed grid has its own Ordered Set.

Variant 2 is implemented in Java, C++ and Kotlin. As of July 2025, the other programming languages that I currently know, do not have an inbuilt Ordered Set.

