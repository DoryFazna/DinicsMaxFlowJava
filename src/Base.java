import java.util.ArrayList;
import java.util.List;
// Source :https://github.com/williamfiset/Algorithms/tree/master/src/main/java/com/williamfiset/algorithms/graphtheory/networkflow/examples
// Comments and code modifications are done by myself.

//UOW ID - w1716295
//Student ID - 2017457



abstract class Base {

    //Setting up a maximum value to find the FIRST bottle neck value.
    static final long INF = Long.MAX_VALUE;

    //total number of nodes, Source node, End node
    final int n,s,t;

    // to confirm the 'end of execution'
    protected boolean solved;

    //to hold the maximum flow value
    protected long maxFlow;

    //The main datastructure which holds the details about nodes and edges.
    protected List<Edge>[] graph;

    //To keep count of used paths.
    protected int paths;

    /**
     * Initializes variables and set up for the execution
     * @param n total number of nodes
     * @param s source node
     * @param t end node
     */
    public Base(int n, int s, int t){
        this.n =n;
        this.s=s;
        this.t = t;
        InitializeEmptyFlowGraph(); // The main data structure is initialized with empty edges.
        paths = 0;  // to count number of paths found
    }

    /**
     * Initialize a flow graph with the length of n, and assign empty ArrayList<Edge>.
     * ArrayList<Edge> will LATER hold the details of Edge starting from a particular Node.
     * ex : (from source) graph[0] - <edge1>,<edge2>,<edge3>
     *      (from node1)  graph[1] - <edge3>,<edge5>
     *      (from node2)  graph[2] - <edge4> ...
     */
    private void InitializeEmptyFlowGraph(){
        graph = new List[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<Edge>();
        }
    }

    /**
     *
     * @param from node where the edge starts from
     * @param to node where the edge ends
     * @param capacity
     */
    public void addEdge(int from,int to,long capacity){
        if(capacity<=0)
            throw new IllegalArgumentException("Forward edge capacity <=0");

        if(from == to){
            throw new IllegalArgumentException("Starting node & ending node are same!");
        }


        //In case where capacity is mentioned from both sides of same edge.
        // ex : node1 -> node2  capacity = 10
        //      node2 -> node1  capacity = 5
        //In this case, residual edge will not get created again.
        for(Edge edge : graph[from]){
            if(edge.from == from && edge.to == to){
                edge.capacity+=capacity;
                return;
            }
        }

        //Create new edge (start node = from, end node= to)
        Edge e1 = new Edge(from, to, capacity);

        //Create a Residual edge with capacity 0 (start node = to, end node = from)
        Edge e2 = new Edge(to, from, 0);

        //assign relevant edges as 'residual' edge.
        //ex : (EDGE from A to B) 's residual edge will be (EDGE from B to A)
        e1.residual = e2;
        e2.residual = e1;

        //Adding the edges in the relevant position in the main Data structure(graph).
        graph[from].add(e1);
        graph[to].add(e2);
    }

    /**
     * to get the whole network flow graph.(node, cpacity, flow details.. )
     * @return the graph ArrayList.
     * graph will be returned after the Execution of finding the MAXFLOW.
     */
    public List[] getGraph(){
        execute();
        return graph;
    }

    /**
     * Maximum flow will be summed up in the solve() method which is called inside execute() method.
     * @return maximum flow
     */
    public long getMaxFlow(){
        execute();
        return maxFlow;
    }

    /**
     * checks if the execution is completed, if NOT completed, it will call the solve() method.
     * solved = true  -> Execution is completed
     * solved = false -> not completed yet
     */
    private void execute(){
        if(solved) return;
        solved=true;
        solve();
    }

    /**
     * @return number of paths found
     */
    public int getPathsCount(){
        execute();
        return paths;
    }




    /**
     * This is the solution CODE for Max flow problem.
     * Declared abstract as there are many other solutions available for Maximum flow problem
     * In this coursework I have implemented Dinic's Algorithm.
     * Check DinicsSolver Class for implementation.
     */
    public abstract void solve();



}


