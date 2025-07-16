
package main
import "container/heap"

const NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1
const MAINTENANCE_CHECK_REQUEST = 1
const GO_OFFLINE_REQUEST = 2

func processQueries(numberOfPowerStations int, connections [][]int, queries [][]int) []int {
    operationalStations := make([]bool, numberOfPowerStations + 1)
    for i := range operationalStations {
        operationalStations[i] = true
    }

    unionFind := NewUnionFind(numberOfPowerStations)
    for _, connection := range connections {
        unionFind.joinByRank(connection[0], connection[1])
    }
    var parentToOperationalStations map[int]*OperationalStations = createParentToOperationalStations(&unionFind, operationalStations, numberOfPowerStations)

    return assignStationsDuringMaintenance(queries, &unionFind, parentToOperationalStations)
}

func createParentToOperationalStations(unionFind *UnionFind, operationalStaions []bool, numberOfPowerStations int) map[int]*OperationalStations {
    parentToOperationalStations := map[int]*OperationalStations{}
    for station := 1; station <= numberOfPowerStations; station++ {
        parent := unionFind.findParent(station)
        if _, has := parentToOperationalStations[parent]; !has {
            parentToOperationalStations[parent] = NewOperationalStaions(operationalStaions)
        }

        parentToOperationalStations[parent].addStation(station)
    }
    return parentToOperationalStations
}

func assignStationsDuringMaintenance(queries [][]int, unionFind *UnionFind, parentToOperationalStations map[int]*OperationalStations) []int {
    resultsOfQueryForMaintenanceCheck := make([]int, 0)
    for _, query := range queries {
        queryType := query[0]
        station := query[1]
        parent := unionFind.findParent(station)

        if queryType == GO_OFFLINE_REQUEST {
            parentToOperationalStations[parent].removeStation(station)
            continue
        }

        if queryType == MAINTENANCE_CHECK_REQUEST {
            result := parentToOperationalStations[parent].getStationToTakeOverDuringMaintenanceCheck(station)
            resultsOfQueryForMaintenanceCheck = append(resultsOfQueryForMaintenanceCheck, result)
        }
    }

    return resultsOfQueryForMaintenanceCheck
}

/***** UnionFind definition start *****/

type UnionFind struct {
    parent []int
    rank   []int
}

func NewUnionFind(numberOfPowerStations int) UnionFind {
    uf := UnionFind{
        parent: make([]int, numberOfPowerStations+1),
        rank:   make([]int, numberOfPowerStations+1),
    }
    for i := 0; i <= numberOfPowerStations; i++ {
        uf.parent[i] = i
        uf.rank[i] = 1
    }
    return uf
}

func (this *UnionFind) findParent(index int) int {
    if this.parent[index] != index {
        this.parent[index] = this.findParent(this.parent[index])
    }
    return this.parent[index]
}

func (this *UnionFind) joinByRank(indexOne int, indexTwo int) {
    first := this.findParent(indexOne)
    second := this.findParent(indexTwo)
    if first == second {
        return
    }

    if this.rank[first] >= this.rank[second] {
        this.parent[second] = first
        this.rank[first] += this.rank[second]
    } else {
        this.parent[first] = second
        this.rank[second] += this.rank[first]
    }
}

/***** UnionFind definition end *****/

/***** OperationalStations definition start *****/

type OperationalStations struct {
    operationalStaions  []bool
    minHeapForStationID PriorityQueue
}

func NewOperationalStaions(operationalStaions []bool) *OperationalStations {
    op := OperationalStations{
        operationalStaions:  operationalStaions,
        minHeapForStationID: PriorityQueue{},
    }
    return &op
}

func (this *OperationalStations) addStation(station int) {
    heap.Push(&this.minHeapForStationID, station)
}

func (this *OperationalStations) removeStation(station int) {
    this.operationalStaions[station] = false
}

func (this *OperationalStations) getStationToTakeOverDuringMaintenanceCheck(station int) int {
    if this.operationalStaions[station] {
        return station
    }

    for this.minHeapForStationID.Len() > 0 && !this.operationalStaions[this.minHeapForStationID.Peek().(int)] {
        heap.Pop(&this.minHeapForStationID)
    }
    if this.minHeapForStationID.Len() == 0 {
        return NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID
    }
    return this.minHeapForStationID.Peek().(int)
}

/***** OperationalStations definition end *****/

/***** PriorityQueue definition start *****/

type PriorityQueue []int

func (pq PriorityQueue) Len() int {
    return len(pq)
}

func (pq PriorityQueue) Less(first int, second int) bool {
    return pq[first] < pq[second]
}

func (pq PriorityQueue) Swap(first int, second int) {
    pq[first], pq[second] = pq[second], pq[first]
}

func (pq *PriorityQueue) Push(object any) {
    *pq = append(*pq, object.(int))
}

func (pq *PriorityQueue) Pop() any {
    value := (*pq)[pq.Len() - 1]
    *pq = (*pq)[0 : pq.Len() - 1]
    return value
}

func (pq *PriorityQueue) Peek() any {
    return (*pq)[0]
}

/***** PriorityQueue definition end *****/
