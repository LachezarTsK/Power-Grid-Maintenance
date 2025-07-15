
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.IntStream;

public class Solution {

    private static final int NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1;
    private static final int MAINTENANCE_CHECK_REQUEST = 1;
    private static final int GO_OFFLINE_REQUEST = 2;

    public int[] processQueries(int numberOfPowerStations, int[][] connections, int[][] queries) {
        UnionFind unionFind = new UnionFind(numberOfPowerStations);
        for (int[] connection : connections) {
            unionFind.joinByRank(connection[0], connection[1]);
        }
        Map<Integer, TreeSet<Integer>> parentToOperationalStations = createParentToOperationalStations(unionFind, numberOfPowerStations);

        return assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations);
    }

    private Map<Integer, TreeSet<Integer>> createParentToOperationalStations(UnionFind unionFind, int numberOfPowerStations) {
        Map<Integer, TreeSet<Integer>> parentToOperationalStations = new HashMap<>();
        for (int station = 1; station <= numberOfPowerStations; ++station) {
            int parent = unionFind.findParent(station);
            parentToOperationalStations.putIfAbsent(parent, new TreeSet<>());
            parentToOperationalStations.get(parent).add(station);
        }
        return parentToOperationalStations;
    }

    private int[] assignStationsDuringMaintenance(int[][] queries, UnionFind unionFind, Map<Integer, TreeSet<Integer>> parentToOperationalStations) {
        List<Integer> resultsOfQueryForMaintenanceCheck = new ArrayList<>();
        for (int[] query : queries) {
            int type = query[0];
            int station = query[1];
            int parent = unionFind.findParent(station);

            if (type == GO_OFFLINE_REQUEST && parentToOperationalStations.get(parent).contains(station)) {
                parentToOperationalStations.get(parent).remove(station);
                continue;
            }

            if (type == MAINTENANCE_CHECK_REQUEST) {
                int result = getOperationalStationToTakeOverDuringMaintenanceCheck(parentToOperationalStations, station, parent);
                resultsOfQueryForMaintenanceCheck.add(result);
            }
        }

        return resultsOfQueryForMaintenanceCheck.stream().mapToInt(n -> n).toArray();
    }

    private int getOperationalStationToTakeOverDuringMaintenanceCheck(Map<Integer, TreeSet<Integer>> parentToOperationalStations, int station, int parent) {
        if (parentToOperationalStations.get(parent).isEmpty()) {
            return NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID;
        }
        if (parentToOperationalStations.get(parent).contains(station)) {
            return station;
        }
        return parentToOperationalStations.get(parent).first();
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
