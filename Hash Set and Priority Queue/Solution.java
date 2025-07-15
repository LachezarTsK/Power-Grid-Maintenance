
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.IntStream;

public class Solution {

    private static final int MAINTENANCE_CHECK_REQUEST = 1;
    private static final int GO_OFFLINE_REQUEST = 2;

    // Power Stations have 1‑based indexing.
    public int[] processQueries(int numberOfPowerStations, int[][] connections, int[][] queries) {
        UnionFind unionFind = new UnionFind(numberOfPowerStations);
        for (int[] connection : connections) {
            unionFind.joinByRank(connection[0], connection[1]);
        }
        Map<Integer, OperationalStations> parentToOperationalStations = createParentToOperationalStations(unionFind, numberOfPowerStations);

        return assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations);
    }

    private Map<Integer, OperationalStations> createParentToOperationalStations(UnionFind unionFind, int numberOfPowerStations) {
        Map<Integer, OperationalStations> parentToOperationalStations = new HashMap<>();
        for (int station = 1; station <= numberOfPowerStations; ++station) {
            int parent = unionFind.findParent(station);
            parentToOperationalStations.putIfAbsent(parent, new OperationalStations());
            parentToOperationalStations.get(parent).addStation(station);
        }
        return parentToOperationalStations;
    }

    private int[] assignStationsDuringMaintenance(int[][] queries, UnionFind unionFind, Map<Integer, OperationalStations> parentToOperationalStations) {
        List<Integer> resultsOfQueryForMaintenanceCheck = new ArrayList<>();
        for (int[] query : queries) {
            int type = query[0];
            int station = query[1];
            int parent = unionFind.findParent(station);

            if (type == GO_OFFLINE_REQUEST) {
                parentToOperationalStations.get(parent).removeStation(station);
                continue;
            }

            if (type == MAINTENANCE_CHECK_REQUEST) {
                int result = parentToOperationalStations.get(parent).getStationToTakeOverDuringMaintenanceCheck(station);
                resultsOfQueryForMaintenanceCheck.add(result);
            }
        }

        return resultsOfQueryForMaintenanceCheck.stream().mapToInt(n -> n).toArray();
    }
}

class UnionFind {

    int[] parent;
    int[] rank;

    UnionFind(int numberOfPowerStations) {
        parent = IntStream.rangeClosed(0, numberOfPowerStations).toArray();
        rank = new int[numberOfPowerStations + 1];
        Arrays.fill(rank, 1);
    }

    int findParent(int index) {
        if (parent[index] != index) {
            parent[index] = findParent(parent[index]);
        }
        return parent[index];
    }

    void joinByRank(int indexOne, int indexTwo) {
        int first = findParent(indexOne);
        int second = findParent(indexTwo);
        if (first == second) {
            return;
        }

        if (rank[first] >= rank[second]) {
            parent[second] = first;
            rank[first] += rank[second];
        } else {
            parent[first] = second;
            rank[second] += rank[first];
        }
    }
}

class OperationalStations {

    private static final int NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1;

    private final Set<Integer> operationalStaions = new HashSet<>();
    private final PriorityQueue<Integer> minHeapForStationID = new PriorityQueue<>();

    void addStation(int station) {
        operationalStaions.add(station);
        minHeapForStationID.add(station);
    }

    void removeStation(int station) {
        if (operationalStaions.contains(station)) {
            operationalStaions.remove(station);
        }
    }

    int getStationToTakeOverDuringMaintenanceCheck(int station) {
        if (operationalStaions.isEmpty()) {
            return NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID;
        }
        if (operationalStaions.contains(station)) {
            return station;
        }

        while (!minHeapForStationID.isEmpty() && !operationalStaions.contains(minHeapForStationID.peek())) {
            minHeapForStationID.poll();
        }
        return minHeapForStationID.peek();
    }
}
