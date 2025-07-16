
import java.util.*

class Solution {

    private companion object {
        const val MAINTENANCE_CHECK_REQUEST = 1
        const val GO_OFFLINE_REQUEST = 2
    }

    // Power Stations have 1‑based indexing.
    fun processQueries(numberOfPowerStations: Int, connections: Array<IntArray>, queries: Array<IntArray>): IntArray {
        val operationalStations = BooleanArray(numberOfPowerStations + 1) { true }
        val unionFind = UnionFind(numberOfPowerStations)
        for (connection in connections) {
            unionFind.joinByRank(connection[0], connection[1])
        }
        val parentToOperationalStations: MutableMap<Int, OperationalStations> =
            createParentToOperationalStations(unionFind, operationalStations, numberOfPowerStations)

        return assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations)
    }

    private fun createParentToOperationalStations(unionFind: UnionFind, operationalStations: BooleanArray, numberOfPowerStations: Int): MutableMap<Int, OperationalStations> {
        val parentToOperationalStations = mutableMapOf<Int, OperationalStations>()
        for (station in 1..<numberOfPowerStations + 1) {
            val parent = unionFind.findParent(station)
            parentToOperationalStations.putIfAbsent(parent, OperationalStations(operationalStations))
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

class OperationalStations(private val inputOperationalStation: BooleanArray) {

    companion object {
        const val NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1
    }

    private var operationalStations: BooleanArray = inputOperationalStation
    private val minHeapForStationID = PriorityQueue<Int>()

    fun addStation(station: Int) {
        minHeapForStationID.add(station)
    }

    fun removeStation(station: Int) {
        operationalStations[station] = false
    }

    fun getStationToTakeOverDuringMaintenanceCheck(station: Int): Int {
        if (operationalStations[station]) {
            return station
        }

        while (!minHeapForStationID.isEmpty() && !operationalStations[minHeapForStationID.peek()]) {
            minHeapForStationID.poll()
        }
        if (minHeapForStationID.isEmpty()) {
            return NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID
        }
        return minHeapForStationID.peek()
    }
}
