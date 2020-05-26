import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Source :https://github.com/williamfiset/Algorithms/tree/master/src/main/java/com/williamfiset/algorithms/graphtheory/networkflow/examples
// Comments and code modifications are done by myself.

//UOW ID - w1716295
//Student ID - 2017457

public class Example {
    static int count=0;     //To keep track of no of Edited files
    static String fileName; //File name which is currently on process

    //Instance of Base class where the Network graph setup and basic functions are defined.
    static Base solver;

    //n = number of nodes
    //s = starting node
    //t = ending node
    static int s,t,n;


    public static void main(String[] args) {

        Example ex = new Example();

        ex.runCode();


    }


    public void runCode(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter file name...");
        fileName = sc.next();

        //Checking if the user has entered the filename with extension, if not, ".txt" will be added
        int len = fileName.length();
        if(len<4 ||(fileName.charAt(len-4) != '.'
                || fileName.charAt(len-3) != 't'
                || fileName.charAt(len-2) != 'x'
                || fileName.charAt(len-1) != 't'))
            fileName = fileName+".txt";


        readFromFile();     //Reads graph details from the File & saves in the 'solver'.

        //Menu starts here....
        while (true){
        System.out.println("\n--------MAXIMUM FLOW SOLUTION---------"
                +"\n  * * * * Dinic's Algorithm * * * *");
        System.out.println("\nA - Calculate Maximum flow"
                +"\nB - Add a new Edge"
                +"\nC - Delete an existing edge"
                +"\nD - Edit capacity of an existing edge"
                +"\nE - Display available edges "
                +"\nF - Open a different file"
                +"\nG - Exit");
        String user = sc.next().toLowerCase();
        switch (user) {


            case "a"://Calculate max flow

                //Getting the maximum flow and display.
                System.out.format(ConsoleColors.PURPLE_BOLD+"%19s"+solver.getMaxFlow(),"Maximum Flow is: ");
                System.out.println();

                //Getting the num of paths found
                int paths = solver.getPathsCount();

                //Displaying the number of paths found from the above arraylist 'paths'.
                System.out.format("%19s","Number of Paths: ");
                System.out.println(paths+ConsoleColors.RESET);

                //Getting the graph after the execution to display the updates and furthur details.
                List<Edge>[] resultGraph = solver.getGraph();

                System.out.println("---------------------------------");
                for (List<Edge> edges : resultGraph) {

                    for (Edge e : edges)
                        System.out.println(ConsoleColors.PURPLE+e.toString(s, t)+ConsoleColors.RESET);

                }
                break;

            case "b": //Add new edge

                displayAvailableNodes();

                System.out.println("New starting node");
                int user_sNode = validate(solver.s,solver.t-1);

                System.out.println("New ending node");
                int user_eNode = validate(solver.s,solver.t);

                System.out.println("Enter new capacity");

                long user_cap = validateLong();

                try {
                    solver.addEdge(user_sNode, user_eNode, user_cap);
                    System.out.println("Success! The new Edge has been added! ");
                    writeToFile();
                }catch (Exception e){
                    System.out.println("Starting node & Ending node are same!");
                }


                break;


            case "c":   //Delete existing edge
                displayAvailableNodes();

                System.out.println("start node :");
                int user_deleteStartNode = validate(solver.s,solver.t-1);

                List<Edge> availableEdges_toDelete = displayAvailableEdges(user_deleteStartNode);
                System.out.println("Enter ending node's value: ");
                int selectedEdgeID_toDelete = validate(availableEdges_toDelete); //when user enters the node, it will return the position of that ending node

                deleteEdge(user_deleteStartNode,selectedEdgeID_toDelete);


                System.out.println("Success! The selected Edge has been deleted ");
                writeToFile();
                break;

            case "d": // Edit edge
                displayAvailableNodes();

                System.out.println("start node :");
                int user_startNode = validate(solver.s,solver.t-1);
                List<Edge> availableEdges = displayAvailableEdges(user_startNode);

                System.out.println("Enter ending node's value: ");
                int selectedEdgeID = validate(availableEdges);


                System.out.println("Enter new capacity");

                long user_capacity = validateLong();

                solver.graph[user_startNode].get(selectedEdgeID).capacity= user_capacity;

                System.out.println("Success! The capacity is set to "+user_capacity);
                writeToFile();

                break;
            case "e":   //Display available nodes & edges
                displayAvailableNodes();
                break;
            case "f":   //Read from a different file
                runCode();
                break;
            case "g":   //Exit
                System.exit(0);
                break;
            default:    //When typing an Incorrect option
                System.out.println("Invalid option..");
                break;
        }

    }}


    public void writeToFile(){
        fileName = "edited"+(++count)+".txt";       //creating a new file (count is used to not hange the previous files)
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(solver.n+" "+solver.s+" "+solver.t+"\n");

            //Getting the number of positive edges, to avoid null pointer error when reading the file
            int total=0;
            for(List<Edge> edges: solver.graph){
                for(Edge e: edges){
                    if(e.capacity!=0)
                        total++;
                }
            }

            //Writing data to the file
            solver.graph[solver.graph.length-1].size();
            int count=0;
            for(List<Edge> edges: solver.graph){
                for(Edge e: edges) {
                    if(e.capacity!=0) {
                        myWriter.write(e.from + " " + e.to + " " + e.capacity);
                        count++;

                        if (!(count == total)) //This is checked to avoid Null pointer errors when reading the file
                            myWriter.write("\n");
                    }

                }
            }
            myWriter.close();
            System.out.println("Updated graph is saved in a new File: "+fileName+"\n\n");
        } catch (IOException e) {
            System.out.println("An error occurred.\n\n");
            e.printStackTrace();
        }
        readFromFile(); //Reads the edited file and replace the new graph
    }


    public void readFromFile(){

        //Getting the network graph's detail by reading the File.
        //First line of the FILE consists of number of nodes, source node and the ending node.
        //rest of the FILE consists the capacity of edges(From node x to node y)
        try {
            File obj = new File(fileName);
            Scanner get = new Scanner(obj);

            n = Integer.parseInt(get.next()); // number of nodes
            s = Integer.parseInt(get.next()); // source node
            t = Integer.parseInt(get.next()); // ending node

            //Instance of DincsSolver class where the unique methods are defined(dfs, bfs etc..).
            solver = new DincsSolver(n,s,t);

            // in each line -> [from node]<SPACE>[to node]<SPACE>[capacity of that edge]
            while (get.hasNextLine()){
                int startNode = Integer.parseInt(get.next());
                int endNode = Integer.parseInt(get.next());
                long capacity =  Double.valueOf(get.next()).longValue();

                //'addEdge()' method takes from node,to node,capacity and assign it to the Data structure(ArrayList)
                solver.addEdge(startNode,endNode,capacity);
            }
            //When the file is not found
        }catch (FileNotFoundException e){
            System.out.println("FILE not found.. Please check the FILE name and try again..");
            runCode();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Something wrong with the FILE... Please check the file and try again..");
            runCode();
        }




    }




    //---------------------------------------------------------------------------------------------------------------

    /**
     * To validate a number within a range
     * @param start - starting value of the range
     * @param end   - ending value of the range
     * @return      -returns the validated input.
     */
    public int validate(int start,int end){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the node's value ("+start+" - "+end+")");
        int user_startNode = validateInteger();

        if(user_startNode>=start && user_startNode<=end){
            return user_startNode;
        }else{
            System.out.println("The node is not available ! try again! ");
            return validate(start,end);

        }
    }


    /**
     * To validate if the chosed edge's to value is available in the graph
     * @param availableEdges - edges which are vailable in the graph
     * @return  - returns the valid edge's id chosen by the user
     */
    public int validate(List<Edge> availableEdges){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the ending node's value of the Edge");
        int user_endNode = validateInteger();


        for (int i = 0; i < availableEdges.size(); i++) {
            if(user_endNode == availableEdges.get(i).to)
                return i;
        }

        System.out.println("This node is not available ! try again! ");
        return validate(availableEdges);


    }

    /**
     * To validate a Long number (when getting the Capacity from the user)
     * @return returns the validated Long value from the user
     */
    public long validateLong(){
        Scanner sc = new Scanner(System.in);

        try {
            long num = sc.nextInt();
            return num;
        }catch(Exception e){
            System.out.println("Please enter a numeric value...");
            return validateLong();
        }
    }

    /**
     * To validate an Integer(To avoid InputMismatch exception)
     * @return  - returns the validated integer
     */
    public int validateInteger(){
        Scanner sc = new Scanner(System.in);

        try {
            int num = sc.nextInt();
            return num;
        }catch(Exception e){
            System.out.println("Please enter a numeric value...");
            return validateInteger();
        }
    }

    /**
     * To display all the available edges starting from a particular node
     * @param startNode - the starting node chosen by the user
     * @return  - returns all the available edges found from startNode
     */
    public List<Edge> displayAvailableEdges(int startNode){
        System.out.println("Available edges are.. :");
        List<Edge> availableEdges = solver.graph[startNode];
        ArrayList<Integer> availableEnds = new ArrayList<>();
        for (Edge e : availableEdges) {
            System.out.println(e.from +" - "+ e.to+ " : capacity - "+e.capacity);
        }
        return availableEdges;

    }

    /**
     * To display all the nodes & edges available in the graph
     */
    public void displayAvailableNodes(){
        System.out.println("Available edges:");
        for (List<Edge> edges: solver.graph) {
            for (Edge e :edges){
                System.out.println(e.from +" - "+e.to+ " capacity: "+e.capacity);
            }

        }

    }

    /**
     * To delete an edge from the graph. It will also delete the residual edge.
     * @param startNode - the starting node's value (which is chosen by the user)
     * @param ID - the ending node's value's position in the List.(returned from validate method)
     */
    public void deleteEdge(int startNode, int ID){
        int endNode =solver.graph[startNode].get(ID).to;

        //Deleting the edge
        solver.graph[startNode].remove(ID);

        //Deleting the Residual edge
        for (int i = 0; i < solver.graph[endNode].size(); i++) {
            if(solver.graph[endNode].get(i).to == startNode){
                solver.graph[endNode].remove(i);
                break;
        }

        }

    }

}
