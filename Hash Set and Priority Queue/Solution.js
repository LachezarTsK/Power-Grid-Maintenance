
// const {PriorityQueue} = require('@datastructures-js/priority-queue');
/*
 PriorityQueue is internally included in the solution file on leetcode.
 When running the code on leetcode it should stay commented out. 
 It is mentioned here just for information about the external library 
 that is applied for this data structure.
 */

/**
 * @param {number} numberOfPowerStations
 * @param {number[][]} connections
 * @param {number[][]} queries
 * @return {number[]}
 */
// Power Stations have 1‑based indexing.
var processQueries = function (numberOfPowerStations, connections, queries) {
    const unionFind = new UnionFind(numberOfPowerStations);
    for (let connection of connections) {
        unionFind.joinByRank(connection[0], connection[1]);
    }

    // Map<Integer, OperationalStations> 
    const parentToOperationalStations = createParentToOperationalStations(unionFind, numberOfPowerStations);

    return assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations);
};

/**
 * @param {UnionFind} unionFind
 * @param {number} numberOfPowerStations
 * @return {Map<Integer, OperationalStations> []}
 */
function createParentToOperationalStations(unionFind, numberOfPowerStations) {
    const parentToOperationalStations = new Map();

    for (let station = 1; station <= numberOfPowerStations; ++station) {
        const parent = unionFind.findParent(station);
        if (!parentToOperationalStations.has(parent)) {
            parentToOperationalStations.set(parent, new OperationalStations());
        }
        parentToOperationalStations.get(parent).addStation(station);
    }
    return parentToOperationalStations;
}

/**
 * @param {number[][]} queries
 * @param {UnionFind} unionFind
 * @param {Map<number, OperationalStations>} parentToOperationalStations
 * @return {number[]} 
 */
function assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations) {
    const resultsOfQueryForMaintenanceCheck = new Array();

    for (let query of queries) {
        const type = query[0];
        const station = query[1];
        const parent = unionFind.findParent(station);
        if (type === Util.GO_OFFLINE_REQUEST) {
            parentToOperationalStations.get(parent).removeStation(station);
            continue;
        }

        if (type === Util.MAINTENANCE_CHECK_REQUEST) {
            const result = parentToOperationalStations.get(parent).getStationToTakeOverDuringMaintenanceCheck(station);
            resultsOfQueryForMaintenanceCheck.push(result);
        }
    }

    return resultsOfQueryForMaintenanceCheck;
}

class UnionFind {

    /**
     * @param {number} numberOfPowerStations
     */
    constructor(numberOfPowerStations) {
        this.parent = Array.from(Array(numberOfPowerStations + 1).keys());
        this.rank = new Array(numberOfPowerStations + 1).fill(1);
    }

    /**
     * @param {number} index
     * @return {number}
     */
    findParent(index) {
        if (this.parent[index] !== index) {
            this.parent[index] = this.findParent(this.parent[index]);
        }
        return parent[index];
    }

    /**
     * @param {number} indexOne
     * @param {number} indexTwo
     * @return {void}
     */
    joinByRank(indexOne, indexTwo) {
        const first = this.findParent(indexOne);
        const second = this.findParent(indexTwo);
        if (first === second) {
            return;
        }

        if (this.rank[first] >= this.rank[second]) {
            this.parent[second] = first;
            this.rank[first] += this.rank[second];
        } else {
            this.parent[first] = second;
            this.rank[second] += this.rank[first];
        }
    }
}

class OperationalStations {

    // Set<number> 
    operationalStations = new Set();
    // PriorityQueue<number>
    minHeapForStationID = new PriorityQueue((x, y) => x - y);

    /**
     * @param {number} station
     * @return {void}
     */
    addStation(station) {
        this.operationalStations.add(station);
        this.minHeapForStationID.enqueue(station);
    }

    /**
     * @param {number} station
     * @return {void}
     */
    removeStation(station) {
        if (this.operationalStations.has(station)) {
            this.operationalStations.delete(station);
        }
    }

    /**
     * @param {number} station
     * @return {number}
     */
    getStationToTakeOverDuringMaintenanceCheck(station) {
        if (this.operationalStations.size === 0) {
            return Util.NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID;
        }
        if (this.operationalStations.has(station)) {
            return station;
        }

        while (!this.minHeapForStationID.isEmpty() && !this.operationalStations.has(this.minHeapForStationID.front())) {
            this.minHeapForStationID.dequeue();
        }
        return this.minHeapForStationID.front();
    }
}

class Util {
    static NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1;
    static MAINTENANCE_CHECK_REQUEST = 1;
    static GO_OFFLINE_REQUEST = 2;
}
