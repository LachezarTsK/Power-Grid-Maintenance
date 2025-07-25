
using System;
using System.Linq;
using System.Collections.Generic;

public class Solution
{
    private static readonly int MAINTENANCE_CHECK_REQUEST = 1;
    private static readonly int GO_OFFLINE_REQUEST = 2;

    // Power Stations have 1‑based indexing.
    public int[] ProcessQueries(int numberOfPowerStations, int[][] connections, int[][] queries)
    {
        UnionFind unionFind = new UnionFind(numberOfPowerStations);
        foreach (int[] connection in connections)
        {
            unionFind.JoinByRank(connection[0], connection[1]);
        }
        Dictionary<int, OperationalStations> parentToOperationalStations = CreateParentToOperationalStations(unionFind, numberOfPowerStations);

        return AssignStationsDuringMaintenance(queries, unionFind, parentToOperationalStations);
    }

    private Dictionary<int, OperationalStations> CreateParentToOperationalStations(UnionFind unionFind, int numberOfPowerStations)
    {
        Dictionary<int, OperationalStations> parentToOperationalStations = new Dictionary<int, OperationalStations>();
        for (int station = 1; station <= numberOfPowerStations; ++station)
        {
            int parent = unionFind.FindParent(station);
            parentToOperationalStations.TryAdd(parent, new OperationalStations());
            parentToOperationalStations[parent].AddStation(station);
        }
        return parentToOperationalStations;
    }

    private int[] AssignStationsDuringMaintenance(int[][] queries, UnionFind unionFind, Dictionary<int, OperationalStations> parentToOperationalStations)
    {
        List<int> resultsOfQueryForMaintenanceCheck = new List<int>();
        foreach (int[] query in queries)
        {
            int type = query[0];
            int station = query[1];
            int parent = unionFind.FindParent(station);

            if (type == GO_OFFLINE_REQUEST)
            {
                parentToOperationalStations[parent].RemoveStation(station);
                continue;
            }

            if (type == MAINTENANCE_CHECK_REQUEST)
            {
                int result = parentToOperationalStations[parent].GetStationToTakeOverDuringMaintenanceCheck(station);
                resultsOfQueryForMaintenanceCheck.Add(result);
            }
        }

        return resultsOfQueryForMaintenanceCheck.ToArray();
    }
}

class UnionFind
{
    int[] parent;
    int[] rank;

    public UnionFind(int numberOfPowerStations)
    {
        parent = Enumerable.Range(0, numberOfPowerStations + 1).ToArray();
        rank = new int[numberOfPowerStations + 1];
        Array.Fill(rank, 1);
    }

    public int FindParent(int index)
    {
        if (parent[index] != index) {
            parent[index] = FindParent(parent[index]);
        }
        return parent[index];
    }

    public void JoinByRank(int indexOne, int indexTwo)
    {
        int first = FindParent(indexOne);
        int second = FindParent(indexTwo);
        if (first == second)
        {
            return;
        }

        if (rank[first] >= rank[second])
        {
            parent[second] = first;
            rank[first] += rank[second];
        }
        else
        {
            parent[first] = second;
            rank[second] += rank[first];
        }
    }
}

class OperationalStations
{

    private static readonly int NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID = -1;

    private readonly HashSet<int> operationalStations = new HashSet<int>();
    private readonly PriorityQueue<int, int> minHeapForStationID = new PriorityQueue<int, int>();

    public void AddStation(int station)
    {
        operationalStations.Add(station);
        minHeapForStationID.Enqueue(station, station);
    }

    public void RemoveStation(int station)
    {
        if (operationalStations.Contains(station))
        {
            operationalStations.Remove(station);
        }
    }

    public int GetStationToTakeOverDuringMaintenanceCheck(int station)
    {
        if (operationalStations.Count == 0)
        {
            return NO_OPERATIONAL_STATION_EXISTS_IN_THIS_GRID;
        }
        if (operationalStations.Contains(station))
        {
            return station;
        }

        while (minHeapForStationID.Count > 0 && !operationalStations.Contains(minHeapForStationID.Peek()))
        {
            minHeapForStationID.Dequeue();
        }
        return minHeapForStationID.Peek();
    }
}
