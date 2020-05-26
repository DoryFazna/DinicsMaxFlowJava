import java.io.Console;
import java.util.*;

import static java.lang.Math.min;

// Source :https://github.com/williamfiset/Algorithms/tree/master/src/main/java/com/williamfiset/algorithms/graphtheory/networkflow/examples
// Comments and code modifications are done by myself.

//UOW ID - w1716295
//Student ID - 2017457

public class DincsSolver extends Base {

    //Array holds the LEVEL for each node.
    private int[] level;

    //Array will contain node indexes, to track the path from Source to Sink to print in console.
    private ArrayList<Integer> path;

    /**
     * Constructor will pass n,s,t to its super class 'Base'
     * Initializes the level array with the length of 'total number of nodes (n)'
     * as each node's level will be checked in upcoming methods.
     * @param n total number of nodes
     * @param s souce node
     * @param t sink node
     */
    public DincsSolver(int n, int s, int t) {
        super(n, s, t);
        level = new int[n];
    }



    /**
     * 'Breadth First Search' is to lable each node with a value of LEVEL.
     * Set up of LEVEL graph
     * @return true -  If a PATH from Source to Sink (a.k.a Sink node has been assigned a LEVEL)
     *         false - If no PATH found from Source to Sink (a.k.a Sink node has no value of LEVEL)
     */
    private boolean bfs(){
        //filling level array with value -1.
        //-1 indicates 'node is not visited when assigning LEVEL'
        Arrays.fill(level,-1);

        //to follow Breadth First Search algorithm.
        Deque<Integer> q = new ArrayDeque<>(n);

        //Starting with Source node, LEVEL of Source node = 0
        q.offer(s);
        level[s] =0;

        //Levelling process runs until queue becomes empty, maximum nodes are Lablled.
        while(!q.isEmpty()){

            //Getting the first value out from queue and named as 'node'
            //then loop through edges which started from that node.
            int node = q.poll();
            for(Edge edge :graph[node]){
                long cap =edge.remainingCapacity();

                //if capacity of that EDGE is >0 AND level is NOT assigned, assign LEVEL value of the next node,
                //+1 from current node's level.
                if(cap>0 && level[edge.to]==-1){ //-1 means level is not assigned yet
                    level[edge.to] = level[node]+1;
                    q.offer(edge.to);
                }
            }
        }
        printLevelArrays(level);
        return level[t]!=-1; // TRUE means, has succesfully levelled til the SINK node.
    }


    /**
     * 'Depth First Search' is used to find a path from SOURCE to SINK
     * dfs() is called recursively when a adjacent node is found, in order to reach SINK
     * @param from start node value
     * @param index passing the INDEX array to keep track of visited edges.
     * @param flow to keep track of bottle neck value.
     * @return returns the bottleneck value. IF NO PATH FOUND, returns 0 (to end the for loop where dfs() is called initially)
     */
    private long dfs(int from, int[] index, long flow){

        //If the path has reached the SINK node(t)
        if (from ==t) {
            path.add((int)flow); //adding the first value of path as the FLOW value
            path.add(from); //then adding the SINK node's value
            return flow; // return flow to the place where it is called (recursive call in line 137)
        }
        //get the num of edges associated the current NODE (from node).
        final int numEdges = graph[from].size();

        //Until all the edges of the current NODE (from node) are visited.
        for(; index[from]<numEdges; index[from]++) {

            //gets the unvisited edge.
            Edge edge = graph[from].get(index[from]);
            long cap = edge.remainingCapacity();

            //checks if that edge is VALID to proceed. means,
            //If capacity > 0 AND edge's level is +1 higher than the curren node. (If edge is in the NEXT LEVEL)
            if (cap > 0 && level[edge.to] == level[from] + 1) {
                //calling dfs() passsing the next node , index array and minimum of Current BottleNeck value, current Edge's capacity
                long bottleNeck = dfs(edge.to, index, min(flow, cap));

                //If the returned value is >0 , means, A path is found. (the bottleneck value is returned)
                if (bottleNeck > 0) {
                    //augment method will substract the flow value from the current flow & Increse that value of the Augment edge.
                    edge.augment(bottleNeck);

                    //Adds the current node to the 'path' array to keep track of path.
                    path.add(from);

                    return bottleNeck; // returns flow
                }
            }
        }
        return 0; // means a path is not found. returns 0 in order to end the for loop.
    }


    /**
     * Solution code starts here.
     */
    @Override
    public void solve() {
        long go = System.nanoTime();

        // new instance of path to keep track of the first Path found.
        path= new ArrayList<>();

        // Array 'INDEX' is to hold the last visited index of the Graph[x] array.
        //This is to prevent from looping through the BLOCKED edges again and again.
        int[] index = new int[n];

        //'Breadth First Search' bfs() is to lable nodes with value of LEVEL.
        //If a path is found from s - t it will return TRUE.
        //Decides if a PATH IS FOUND By checking if the Sink node has a value for LEVEL
        while (bfs()){
            //Refreshes the INDEX array with value 0, means the program will loop from the 0th index of each node's Array. (Graph[x])
            Arrays.fill(index,0);

            //'Depth First Search' dfs() is to find a path from Source to Sink with some coditions.(Check doc comment of dfs())
            //This loop runs until dfs() return 0.
            //f holds the Bottle neck value If a path is found.
            //else, 0 will be assigned to f. (To end the for loop)
            for(long f= dfs(s,index,INF); f!=0; f=dfs(s,index,INF)){


                //Maximum flow will get increased by adding up every 'bottleNeck' value(f) [when path from s to t is found.]
                maxFlow +=f;

                //printing the path in the console
                printpath(path);

                //Initializing with new Array in order to store the new Path.
                path = new ArrayList<>();
            }

        }

        long stop = System.nanoTime();


        System.out.format(ConsoleColors.PURPLE_BOLD+"%19s","time elapsed: ");
        System.out.println((stop-go)+" nano seconds"+ConsoleColors.RESET);
        //When the MAXFLOW execution is done, this function will format the paths user Friendly with ARROWS and values.
    }

    /**
     * This method arranges the found path in correct order and
     * converts as String values in user-friendly manner with Arrow representation.
     */

    public void printpath(ArrayList<Integer> path){
        paths ++;

        String pathFlowString = "flow = ";
        pathFlowString +=Integer.toString(path.remove(0)); // first element is the FLOW value.
        pathFlowString += "\npath = ";

        // accessing elements in REVERSE order as it is added from SINK to source. (recursively found the SINK)
        for (int i = path.size(); i>0; i--) {
            if(i!=path.size())
                pathFlowString+= " -> ";
            pathFlowString+= path.get(i-1);

        }
        System.out.println(ConsoleColors.YELLOW_BOLD+pathFlowString);
        System.out.println(ConsoleColors.RESET);



    }


    public void printLevelArrays(int[] level){


        System.out.println(ConsoleColors.CYAN_BOLD_BRIGHT+"LEVEL GRAPH");
        System.out.println("-------------");
        System.out.print("level");
        int len = level.length;
        for (int i = 0; i < len; i++) {
            System.out.format("%3s",level[i]);
            System.out.print(" ");
        }
        System.out.println();

        System.out.print("node ");
        for (int i = 0; i < len; i++) {
            System.out.format("%3s",i);
            System.out.print(" ");
        }
        System.out.println(ConsoleColors.RESET+"\n");


    }



}

