package unsw.gloriaromanus.movement;

import java.util.ArrayList;
import java.util.List;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;


public class Vertex {
    private Province province;
    private List<Vertex> edges;
    private int noOfEdges;
    /**
     * Creates a vertex
     * @param province
     */
    public Vertex(Province province) {
        this.province = province;
        edges = new ArrayList<>();
    }

    /**
     * Adds neighbours of a vertex
     * @param v
     */
    public void addEdge(Vertex v) {
        edges.add(v);
        noOfEdges++;
    }

    /**
     * 
     * @return the number of edges the vertex has
     */
    public int getNoOfEdges() {
        return noOfEdges;
    }

    /**
     * 
     * @param i
     * @return edge #i
     */
	public Vertex getEdge(int i) {
		return edges.get(i);
	}

    /**
     * 
     * @param f
     * @return if f can access a the vertex's province
     */
	public boolean accessible(Faction f) {
		return f.containsProvince(province);
	}

    /**
     * 
     * @return the province of the vertex
     */
    public Province getProvince() {
        return province;
    }


}
