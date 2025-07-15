
// const {PriorityQueue} = require('@datastructures-js/priority-queue');
/*
 PriorityQueue is internally included in the solution file on leetcode.
 When running the code on leetcode it should stay commented out. 
 It is mentioned here just for information about the external library 
 that is applied for this data structure.
 */

// Power Stations have 1‑based indexing.
function processQueries(numberOfPowerStations: number, connections: number[][], queries: number[][]): number[] {
    const unionFind = new UnionFind(numberOfPowerStations);
    for (let connection of connections) {
        unionFind.joinByRank(connection[0], connection[1]);
    }

    const parentToOperationalStations: Map<number, OperationalStations> = createParentToOperationalStations(unionFind, numberOfPowerStations);

    return assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations);
};

function createParentToOperationalStations(unionFind: UnionFind, numberOfPowerStations: number): Map<number, OperationalStations> {
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

function assignStationsDuringMaintenance(queries: number[][], unionFind: UnionFind, parentToOperationalStations: Map<number, OperationalStations>) {
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

    parent: number[];
    rank: number[];

    constructor(numberOfPowerStations: number) {
        this.parent = Array.from(Array(numberOfPowerStations + 1).keys());
        this.rank = new Array(numberOfPowerStations + 1).fill(1);
    }

    findParent(index: number): number {
        if (this.parent[index] != index) {
            this.parent[index] = this.findParent(this.parent[index]);
        }
        return this.parent[index];
    }

    joinByRank(indexOne: number, indexTwo: number): void {
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

    operationalStations = new Set<number>();
    minHeapForStationID = new PriorityQueue<number>((x, y) => x - y);

    addStation(station: number): void {
        this.operationalStations.add(station);
        this.minHeapForStationID.enqueue(station);
    }

    removeStation(station: number): void {
        if (this.operationalStations.has(station)) {
            this.operationalStations.delete(station);
        }
    }

    getStationToTakeOverDuringMaintenanceCheck(station: number): number {
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
