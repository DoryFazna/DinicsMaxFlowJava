// Source :https://github.com/williamfiset/Algorithms/tree/master/src/main/java/com/williamfiset/algorithms/graphtheory/networkflow/examples
// Comments and code modifications are done by myself.

//UOW ID - w1716295
//Student ID - 2017457

public class Edge {

    //start node, end node
    public int from,to;

    //The residual edge will be assigned. (edge from end node - start node)
    public Edge residual;

    //The current flow value of the edge. (Initially 0)
    public long flow;

    //Capacity of the edge (Assigned when creating the Edge OR when creating the Residual edge)
    public long capacity;

    /**
     * Constructor will initialize the values.
     * @param from start node
     * @param to end node
     * @param capacity the capacity of that EDGE.
     */
    public Edge(int from,int to,long capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
    }


    /**
     * Will calculate the Available space of the edge. (total capacity - current flow)
     * @return remaining capacity.
     */
    public long remainingCapacity(){
        return capacity-flow;
    }

    /**
     * This method will substract the flow and increase the current flow of the residual Edge
     * @param bottleNeck flow which needs to be added / substracted
     */
    public void augment(long bottleNeck){
        flow += bottleNeck;
        residual.flow -= bottleNeck;
    }

    //to print the graph detail
    public String toString(int s, int t) {
        String u =(from ==s)?"s":((from==t)?"t":String.valueOf(from));
        String v =(to ==s)?"s":((to==t)?"t":String.valueOf(to));
        return String.format("Edge %s -> %s | flow = %3d | capacity = %3d",
                u,v,flow,capacity);
    }
}
