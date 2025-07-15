# Power-Grid-Maintenance
Challenge at LeetCode.com. Tags: Union Find, Graph, Heap, Priority Queue, Hash Set, Ordered Set.

---------------------------------------------------------------------------------------------------------------------------------------------------------------

The presented solution is in two variants, depending on way the information about operational stations is processed.

**Variant 1**

The most up to date information about the operational stations is stored in a Hash Set. To access quickly the operational station with the smallest ID (if the station taken for maintenance is off grid), a Priority Queue also stores the operational stations ID. 

However, the information stored in the Priority Queue is not necessarily up to date. It is updated lazily. When the operational station with the smallest ID is needed and station at the top the Priority Queue is also contained in the Hash Set, then the ID of this station is returned. Otherwise, the Priority Queue pops up stations until the station at the top is also contained in the Hash Set. If there are no operational stations left, the answer is -1.

Variant 1 is implemented in Java, JavaScript, TypeScript, C++, C#, Kotlin and Golang.

**Variant 2**

The most up to date information about the operational stations is stored in an Ordered Set. When the operational station with the smallest ID is needed, the ID of the station at the top of the Ordered Set is returned. And again, if there are no operational stations left, the answer is -1.

Variant 2 is implemented in Java, C++ and Kotlin. As of July 2025, the other programming languages that I currently know, do not have an inbuilt Ordered Set.
