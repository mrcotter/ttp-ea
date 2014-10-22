package jmetal.problems;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationArrayIntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Permutation;
import jmetal.util.JMException;

import java.io.*;

/**
 * Class representing a multi-objective TTP (Traveling Thief Problem) problem.
 */

public class TTP extends Problem {

    //public String      name;
    //public String      knapsackDataType;
    public int         numberOfNodes;
    public int         numberOfItems;
    public long        capacityOfKnapsack;
    public double      minSpeed;
    public double      maxSpeed;
    public double      rentingRatio;
    //public String      edgeWeightType;
    public double[][]  nodes;
    public int[][]     items;

    public long [][] distanceMatrix_;

    /**
     * Creates a new TTP problem instance. It accepts data files from a ttp file
     * @throws IOException
     */
    public TTP(String solutionType, String fileName) throws IOException {
        numberOfVariables_  = 2;
        numberOfObjectives_ = 2;
        numberOfConstraints_= 0;
        problemName_        = "TTP";

        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];

        for (int var = 0; var < numberOfVariables_; var++){
            lowerLimit_[var] = 0.0;
            upperLimit_[var] = 1.0;
        }

        length_ = new int[numberOfVariables_];

        distanceMatrix_ = readProblem(fileName) ;

        System.out.println(numberOfNodes) ;
        System.out.println(numberOfItems) ;
        length_      [0] = numberOfNodes ;
        length_      [1] = numberOfItems;

        if (solutionType.compareTo("PermutationArrayInt") == 0)
            solutionType_ = new PermutationArrayIntSolutionType(this, 1, 1) ;
        else {
            System.out.println("Error: solution type " + solutionType + " invalid") ;
            System.exit(-1) ;
        }
    } // TTP

    /**
     * Evaluates a solution
     * @param solution The solution to evaluate
     */
    @Override
    public void evaluate(Solution solution) throws JMException {

        int[] tour = new int[numberOfNodes + 1];
        int firstCity = ((Permutation) solution.getDecisionVariables()[0]).vector_[0];
        int lastCity  = ((Permutation) solution.getDecisionVariables()[0]).vector_[numberOfNodes - 1];

        double fitness1_td = 0.0;

        // Calculate fitness 1 - total distance
        for (int i = 0; i < numberOfNodes - 1; i++) {
            int x = ((Permutation) solution.getDecisionVariables()[0]).vector_[i];
            int y = ((Permutation) solution.getDecisionVariables()[0]).vector_[i+1];

            tour[i] = x;
            tour[i+1] = y;

            fitness1_td += distanceMatrix_[x][y];
        }
        // Repair - generate tour with same start city and end city
        tour[numberOfNodes] = tour[0];

        fitness1_td += distanceMatrix_[firstCity][lastCity];


        // Correctness check: does the tour start and end in the same city
        if(tour[0] != tour[tour.length - 1]) {
            System.err.println("ERROR: The last city must be the same as the first city");
            System.exit(1);
        }


        // Calculate fitness 2 - objective value of a given tour
        ArrayInt z = (ArrayInt) solution.getDecisionVariables()[1];

        double fitness2_ob;
        double wc = 0.0;
        double ft = 0.0;
        double fp = 0.0;

        //the following is used for a different interpretation of "packingPlan"
        int itemsPerCity = z.getLength() / (tour.length - 2);

        for (int i = 0; i < tour.length - 1; i++) {

            int currentCityTEMP = tour[i];
            // what's the current city? --> but the items start at city 2 in the TTP file, so I have to take another 1 off!
            int currentCity = currentCityTEMP - 1;

            if (i > 0) {
                for (int itemNumber = 0; itemNumber < itemsPerCity; itemNumber++) {

                    int indexOfPackingPlan = (i-1) * itemsPerCity + itemNumber;
                    // what is the next item's index in items-array?
                    int itemIndex = currentCity + itemNumber * (numberOfNodes - 1);

                    if (z.getValue(indexOfPackingPlan) == 1) {
                        // pack item
                        int currentWC = items[itemIndex][2];
                        wc += currentWC;

                        int currentFP = items[itemIndex][1];
                        fp += currentFP;
                    }
                }

                int h = (i+1) % (tour.length-1); //h: next tour city index
                long distance = distanceMatrix_[i][h];
                // compute the adjusted (effective) distance
                ft += (distance / (1 - wc * (maxSpeed - minSpeed) / capacityOfKnapsack));
            }
        }

        fitness2_ob = fp - ft * rentingRatio;

        solution.setObjective(0, fitness1_td);
        solution.setObjective(1, -fitness2_ob); // Convert from maximum objective value to minimum objective value
    }


    private long[][] readProblem(String fileName) throws IOException {

        long[][] matrix;
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(fileName)));

        try {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line

                /*if (line.startsWith("PROBLEM NAME")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.name = line;
                }
                if (line.startsWith("KNAPSACK DATA TYPE")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.knapsackDataType = line;
                }*/
                if (line.startsWith("DIMENSION")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.numberOfNodes=Integer.parseInt(line);
                }
                if (line.startsWith("NUMBER OF ITEMS")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.numberOfItems=Integer.parseInt(line);
                }
                if (line.startsWith("CAPACITY OF KNAPSACK")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.capacityOfKnapsack=Long.parseLong(line);
                }
                if (line.startsWith("MIN SPEED")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.minSpeed=Double.parseDouble(line);
                }
                if (line.startsWith("MAX SPEED")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.maxSpeed=Double.parseDouble(line);
                }
                if (line.startsWith("RENTING RATIO")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.rentingRatio=Double.parseDouble(line);
                }
                /*if (line.startsWith("EDGE_WEIGHT_TYPE")) {
                    line = line.substring(line.indexOf(":")+1);
                    line = line.replaceAll("\\s+","");
                    this.edgeWeightType = line;
                }*/
                if (line.startsWith("NODE_COORD_SECTION")) {
                    this.nodes = new double[this.numberOfNodes][3];
                    for (int i=0; i<this.numberOfNodes; i++) {
                        line = br.readLine();
                        String[] splittedLine = line.split("\\s+");
                        for (int j=0; j<splittedLine.length; j++) {
                            double temp = Double.parseDouble(splittedLine[j]);
                            if (j==0) temp =  temp-1;
                            this.nodes[i][j] = temp;
                        }
                    }
                }
                if (line.startsWith("ITEMS SECTION")) {
                    this.items = new int[this.numberOfItems][4];
                    for (int i=0; i<this.numberOfItems; i++) {
                        line = br.readLine();
                        String[] splittedLine = line.split("\\s+");
                        for (int j=0; j<splittedLine.length; j++) {
                            int temp = Integer.parseInt(splittedLine[j]);
                            // adjust city number by 1
                            if (j==0) temp =  temp-1;  // item numbers start here with 0 --> in TTP files with 1
                            if (j==3) temp =  temp-1;  // city numbers start here with 0 --> in TTP files with 1
                            this.items[i][j] = temp;
                        }
                    }
                }
            }
            br.close();

        } catch (Exception ex) {
            System.err.println ("TTP.readProblem(): error when reading data file " + ex);
            System.exit(1);
        }

        double dist;
        long distance;
        matrix = new long[numberOfNodes][numberOfNodes];

        for (int i = 0; i < numberOfNodes; i++) {
            matrix[i][i] = 0;
            for (int j = i + 1; j < numberOfNodes; j++) {
                dist = Math.sqrt(
                                (this.nodes[i][1]-this.nodes[j][1]) *
                                (this.nodes[i][1]-this.nodes[j][1]) +
                                (this.nodes[i][2]-this.nodes[j][2]) *
                                (this.nodes[i][2]-this.nodes[j][2]));
                distance = (long) Math.ceil(dist);
                matrix[i][j] = distance;
                matrix[j][i] = distance;
            }
        }

        return matrix;
    }
}
