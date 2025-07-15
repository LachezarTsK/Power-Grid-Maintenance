
#include <set>
#include <span>
#include <vector>
#include <unordered_map>
using namespace std;

class UnionFind {

    vector<int> parent;
    vector<int> rank;

public:
    UnionFind(int numberOfPowerStations) {
        parent.resize(numberOfPowerStations + 1);
        ranges::iota(parent, 0);
        rank.assign(numberOfPowerStations + 1, 1);
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
        }
        else {
            parent[first] = second;
            rank[second] += rank[first];
        }
    }
};

class Solution {

    static const int NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1;
    static const int MAINTENANCE_CHECK_REQUEST = 1;
    static const int GO_OFFLINE_REQUEST = 2;

    // Power Stations have 1‑based indexing.
public:
    vector<int> processQueries(int numberOfPowerStations, vector<vector<int>>& connections, vector<vector<int>>& queries) const {
        UnionFind unionFind(numberOfPowerStations);
        for (const auto& connection : connections) {
            unionFind.joinByRank(connection[0], connection[1]);
        }
        unordered_map<int, set<int>> parentToOperationalStations = createParentToOperationalStations(unionFind, numberOfPowerStations);

        return assignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations);
    }

private:
    unordered_map<int, set<int>> createParentToOperationalStations(UnionFind& unionFind, int numberOfPowerStations) const {
        unordered_map<int, set<int>> parentToOperationalStations;
        for (int station = 1; station <= numberOfPowerStations; ++station) {
            int parent = unionFind.findParent(station);
            parentToOperationalStations[parent].insert(station);
        }
        return parentToOperationalStations;
    }

    vector<int> assignStationsDuringMaintenance(span<const vector<int>> queries, UnionFind& unionFind, unordered_map<int, set<int>>& parentToOperationalStations) const {
        vector<int> resultsOfQueryForMaintenanceCheck;
        for (const auto& query : queries) {
            int type = query[0];
            int station = query[1];
            int parent = unionFind.findParent(station);

            if (type == GO_OFFLINE_REQUEST && parentToOperationalStations[parent].contains(station)) {
                parentToOperationalStations[parent].erase(station);
                continue;
            }

            if (type == MAINTENANCE_CHECK_REQUEST) {
                int result = getOperationalStationToTakeOverDuringMaintenanceCheck(parentToOperationalStations, station, parent);
                resultsOfQueryForMaintenanceCheck.push_back(result);
            }
        }

        return resultsOfQueryForMaintenanceCheck;
    }

    int getOperationalStationToTakeOverDuringMaintenanceCheck(const unordered_map<int, set<int>>& parentToOperationalStations, int station, int parent) const {
        if (parentToOperationalStations.at(parent).empty()) {
            return NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID;
        }
        if (parentToOperationalStations.at(parent).contains(station)) {
            return station;
        }
        return *parentToOperationalStations.at(parent).cbegin();
    }
};
