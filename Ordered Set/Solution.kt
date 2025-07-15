
import java.util.*;

class Solution {

    private companion object {
        const val NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1
        const val MAINTENANCE_CHECK_REQUEST = 1
        const val GO_OFFLINE_REQUEST = 2
    }

    // Power Stations have 1‑based indexing.
    fun processQueries(numberOfPowerStations: Int, connections: Array<IntArray>, queries: Array<IntArray>): IntArray {
        val unionFind = UnionFind(numberOfPowerStations)
        for (connection in connections) {
            unionFind.joinByRank(connection[0], connection[1])
        }
        val parentToOperationalStations: MutableMap<Int, SortedSet<Int>> =
            createParentToOperationalStations(unionFind, numberOfPowerStations)

        return assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations)
    }

    private fun createParentToOperationalStations(unionFind: UnionFind, numberOfPowerStations: Int): MutableMap<Int, SortedSet<Int>> {
        val parentToOperationalStations = mutableMapOf<Int, SortedSet<Int>>()
        for (station in 1..<numberOfPowerStations + 1) {
            val parent = unionFind.findParent(station)
            parentToOperationalStations.putIfAbsent(parent, sortedSetOf())
            parentToOperationalStations[parent]!!.add(station)
        }

        return parentToOperationalStations
    }

    private fun assignStationsDuringMaintenance(queries: Array<IntArray>, unionFind: UnionFind, parentToOperationalStations: MutableMap<Int, SortedSet<Int>>): IntArray {
        val resultsOfQueryForMaintenanceCheck = mutableListOf<Int>()
        for (query in queries) {
            val type = query[0]
            val station = query[1]
            val parent = unionFind.findParent(station)

            if (type == GO_OFFLINE_REQUEST && parentToOperationalStations[parent]!!.contains(station)) {
                parentToOperationalStations[parent]!!.remove(station)
                continue
            }

            if (type == MAINTENANCE_CHECK_REQUEST) {
                val result =
                    getOperationalStationToTakeOverDuringMaintenanceCheck(parentToOperationalStations, station, parent)
                resultsOfQueryForMaintenanceCheck.add(result)
            }
        }

        return resultsOfQueryForMaintenanceCheck.toIntArray()
    }

    private fun getOperationalStationToTakeOverDuringMaintenanceCheck(parentToOperationalStations: MutableMap<Int, SortedSet<Int>>, station: Int, parent: Int): Int {
        if (parentToOperationalStations[parent]!!.isEmpty()) {
            return NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID
        }
        if (parentToOperationalStations[parent]!!.contains(station)) {
            return station
        }
        return parentToOperationalStations[parent]!!.first()
    }
}

class UnionFind(private val numberOfPowerStations: Int) {

    private var parent: IntArray = IntArray(numberOfPowerStations + 1) { i -> i }
    private var rank: IntArray = IntArray(numberOfPowerStations + 1) { 1 }

    fun findParent(index: Int): Int {
        if (parent[index] != index) {
            parent[index] = findParent(parent[index]);
        }
        return parent[index];
    }

    fun joinByRank(indexOne: Int, indexTwo: Int) {
        val first = findParent(indexOne)
        val second = findParent(indexTwo)
        if (first == second) {
            return
        }

        if (rank[first] >= rank[second]) {
            parent[second] = first
            rank[first] += rank[second]
        } else {
            parent[first] = second
            rank[second] += rank[first]
        }
    }
}
