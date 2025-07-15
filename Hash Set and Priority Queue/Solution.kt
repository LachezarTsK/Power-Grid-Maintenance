
import java.util.*

class Solution {

    private companion object {
        const val MAINTENANCE_CHECK_REQUEST = 1
        const val GO_OFFLINE_REQUEST = 2
    }

    // Power Stations have 1‑based indexing.
    fun processQueries(numberOfPowerStations: Int, connections: Array<IntArray>, queries: Array<IntArray>): IntArray {
        val unionFind = UnionFind(numberOfPowerStations)
        for (connection in connections) {
            unionFind.joinByRank(connection[0], connection[1])
        }
        val parentToOperationalStations: MutableMap<Int, OperationalStations> =
            createParentToOperationalStations(unionFind, numberOfPowerStations)

        return assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations)
    }

    private fun createParentToOperationalStations(unionFind: UnionFind, numberOfPowerStations: Int): MutableMap<Int, OperationalStations> {
        val parentToOperationalStations = mutableMapOf<Int, OperationalStations>()
        for (station in 1..<numberOfPowerStations + 1) {
            val parent = unionFind.findParent(station)
            parentToOperationalStations.putIfAbsent(parent, OperationalStations())
            parentToOperationalStations[parent]!!.addStation(station)
        }
        return parentToOperationalStations
    }

    private fun assignStationsDuringMaintenance(queries: Array<IntArray>, unionFind: UnionFind, parentToOperationalStations: MutableMap<Int, OperationalStations>): IntArray {
        val resultsOfQueryForMaintenanceCheck = mutableListOf<Int>()
        for (query in queries) {
            val type = query[0]
            val station = query[1]
            val parent = unionFind.findParent(station)

            if (type == GO_OFFLINE_REQUEST) {
                parentToOperationalStations[parent]!!.removeStation(station)
                continue
            }

            if (type == MAINTENANCE_CHECK_REQUEST) {
                val result = parentToOperationalStations[parent]!!.getStationToTakeOverDuringMaintenanceCheck(station)
                resultsOfQueryForMaintenanceCheck.add(result)
            }
        }

        return resultsOfQueryForMaintenanceCheck.toIntArray()
    }
}

class UnionFind(private val numberOfPowerStations: Int) {

    private var parent: IntArray = IntArray(numberOfPowerStations + 1) { i -> i }
    private var rank: IntArray = IntArray(numberOfPowerStations + 1) { 1 }

    fun findParent(index: Int): Int {
        var index = index
        while (parent[index] != index) {
            index = parent[parent[index]]
        }
        return parent[index]
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

class OperationalStations {

    companion object {
        const val NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1
    }

    private val operationalStations = HashSet<Int>()
    private val minHeapForStationID = PriorityQueue<Int>()

    fun addStation(station: Int) {
        operationalStations.add(station)
        minHeapForStationID.add(station)
    }

    fun removeStation(station: Int) {
        if (operationalStations.contains(station)) {
            operationalStations.remove(station)
        }
    }

    fun getStationToTakeOverDuringMaintenanceCheck(station: Int): Int {
        if (operationalStations.isEmpty()) {
            return NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID
        }
        if (operationalStations.contains(station)) {
            return station
        }

        while (!minHeapForStationID.isEmpty() && !operationalStations.contains(minHeapForStationID.peek())) {
            minHeapForStationID.poll()
        }
        return minHeapForStationID.peek()
    }
}
