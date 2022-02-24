package unsw.gloriaromanus.movement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.json.JSONObject;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;

public class Graph {

    private static String PATH = "src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json";

    private Map<String, Vertex> vertices = new HashMap<>();
    private List<Vertex> allVertices = new ArrayList<>();

    /**
     * Creates a graph with the provinces
     * @param provinces
     */
    public Graph(List<Province> provinces) {
        createVertices(provinces);
        addEdges(provinces);
    }

    /**
     * Used to test with a simpler graph
     * @param p
     */
    public static void setPath(String p) {
        PATH = p;
    }

    /**
     * Creates a vertex for each province
     * @param provinces
     */
    public void createVertices(List<Province> provinces) {
        for (Province p: provinces) {
            vertices.put(p.getName(), new Vertex(p));
            allVertices.add(vertices.get(p.getName()));
        }
    }

    /**
     * Adds edges to all vertices
     * @param provinces
     */
    public void addEdges(List<Province> provinces) {
        try{
            // System.out.println("Adding Edges");
            String content = Files.readString(Paths.get(PATH));
            JSONObject provinceAdjacencyMatrix = new JSONObject(content);
            // System.out.println("Entering province loop");

            for (Province p: provinces) {
                JSONObject pAdjacentMatric = provinceAdjacencyMatrix.getJSONObject(p.getName());
                // System.out.println("\tGetting Keys of "+p.getName());
                var nodes = pAdjacentMatric.keys();
                while (nodes.hasNext()) {
                    String node = nodes.next();
                    // System.out.println("\t\tName is: "+node);
                    if (pAdjacentMatric.getBoolean(node)) {
                        Vertex v1 = vertices.get(p.getName());
                        // System.out.println("\t\t\tMain: "+v1.getProvince().getName());
                        Vertex v2 = vertices.get(node);
                        // System.out.println("\t\t\tNeighbor: "+v2.getProvince().getName());
                        v1.addEdge(v2);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the shortest path from start to dest which the faction f can access
     * @param f
     * @param start
     * @param dest
     * @return Shortest path
     */
    public List<Province> getShortestPath(Faction f, Province start, Province dest) {
        Vertex s = vertices.get(start.getName());
        Vertex d = vertices.get(dest.getName());

        Map<Vertex, Vertex> pred = new HashMap<>();
        Map<Vertex, Integer> distance = new HashMap<>();

        List<Province> path = new ArrayList<>();
        if (Boolean.FALSE.equals(BFS(f, s, d, pred, distance))) {
            System.out.println("\tBFS Failed!");
            return path;
        }

        Vertex backTrack = d;
        while (pred.get(backTrack) != null) {
            path.add(0, backTrack.getProvince());
            backTrack = pred.get(backTrack);
        }

        return path;
    }

    /**
     * Does a breadth first search and fills in the dest and pred maps
     * @param f
     * @param start
     * @param dest
     * @param pred
     * @param dist
     * @return
     */
    public Boolean BFS(Faction f, Vertex start, Vertex dest, Map<Vertex, Vertex> pred, Map<Vertex, Integer> dist) {

        List<Vertex> visited = new ArrayList<>();

        Queue<Vertex> q = new LinkedList<>();

        for (Vertex v: allVertices) {
            dist.put(v, Integer.MAX_VALUE);
            pred.put(v, null);
        }
        visited.add(start);
        dist.replace(start, 0);
        q.add(start);
        while(!q.isEmpty()) {
            Vertex v = q.poll();

            for (int i = 0; i < v.getNoOfEdges(); i++) {
                Vertex vn = v.getEdge(i);
                if (!visited.contains(vn) && vn.accessible(f)) {
                    visited.add(vn);
                    dist.replace(vn, dist.get(v) + 4);
                    pred.replace(vn, v);
                    q.add(vn);

                    if (vn.equals(dest)) return true;
                }
            }
        }

        return false;
    }

}
