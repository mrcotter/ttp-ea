package ttp.Optimisation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ttp.TTPInstance;
import ttp.TTPSolution;
import ttp.Node;
import ttp.Utils.DeepCopy;

public class CrossOverTTP {
	
	private ArrayList<ArrayList<Node>> multiPlans = new ArrayList<ArrayList<Node>>();;
	private ArrayList<Double> multiOBs = new ArrayList<Double>();
	private ArrayList<ArrayList<Node>> offsprings = new ArrayList<ArrayList<Node>>();
	private ArrayList<TTPSolution> multiSols = new ArrayList<TTPSolution>();
	private int[] tour;
	private TTPInstance instance;
	
	public CrossOverTTP(TTPInstance instance, int[] tour) {
		
			this.tour = (int[])DeepCopy.copy(tour);
			this.instance = instance;
	}
	
	
	public TTPSolution RunGA(int popSize, int generations, double cross_rate, int MaxRunTime){
		
		TTPSolution bestSolution = null;
		
		ArrayList<Double> fixedOBs = new ArrayList<Double>(multiOBs);
		
		double bestObjective = Collections.max(fixedOBs);
        
        bestSolution = multiSols.get(multiOBs.indexOf(bestObjective));
        
        
        long startingTimeForRuntimeLimit = System.currentTimeMillis()-200;
        
        for(int i = 0; i < generations; i++){            
        	
        	GA_Plan(multiPlans, cross_rate);
        	
        	for(int j = 0; j < multiPlans.size(); j++){       		
        		TTPSolution newSolution = new TTPSolution(tour, ToIntegerArray(multiPlans.get(j)));
                instance.evaluate(newSolution);
                
                if (newSolution.ob >= bestObjective && newSolution.wend >=0 ) {
                	
                	bestSolution = newSolution;
                }
                
                if (i%10==0 && (System.currentTimeMillis()-startingTimeForRuntimeLimit)>=MaxRunTime){
            		break;
            	}  
        		
        	}
        }
		
		
		return bestSolution;
	}
	
	//
	private void GA_Plan(ArrayList<ArrayList<Node>> pop, double cross_rate){
		
		ArrayList<ArrayList<Node>> nextGeneration = new ArrayList<ArrayList<Node>>(pop.size());
		ArrayList<Node> parent1 = null;
		ArrayList<Node> parent2 = null;
		
		int popSize = pop.size();
		
		for(int j = 0; j < popSize/2; j++){
			
			if(popSize > 5) {
				parent1 = Selection_Tournament(5, pop);
				parent2 = Selection_Tournament(5, pop);
			}
			else{
				parent1 = multiPlans.get(0);
				parent2 = multiPlans.get(1);
			}
			
			
			if(Math.random() <= cross_rate){
				
				Crossover_PMX(parent1, parent2);
				Crossover_PMX(parent2, parent1);

                nextGeneration.add(offsprings.get(0));
                nextGeneration.add(offsprings.get(1));
				
			} else {
                nextGeneration.add(parent1);
                nextGeneration.add(parent2);
			}
			
			offsprings.clear();		
		}
		
		multiPlans.clear();
		
		multiPlans = nextGeneration; 		
	}
	
	//
	public void generatePopulation(TTPSolution sol){
		
		multiPlans.add(sol.packList);
		multiOBs.add(sol.ob);
		multiSols.add(sol);	
	}
	
	//
	private void Crossover_PMX(ArrayList<Node> plan_1, ArrayList<Node> plan_2){
		
		int size = plan_1.size();

        Random rand = new Random();
        int number1, number2;
        //Choose two arbitrary parts for start and end from parents
        do {
            number1 = rand.nextInt(size);
            number2 = rand.nextInt(size);
        } while (number1 == number2);
        
        
        int start = Math.min(number1, number2);
        int end = Math.max(number1, number2);
        //System.out.println("start: " + start + ", end: " + end);

        //Initialise children
        List<Node> child_1 = Arrays.asList(new Node[size]);
        List<Node> child_2 = Arrays.asList(new Node[size]);

        //Copy the section from the parents to the children
        for (int i = start; i <= end; i++) {
            child_1.set(i, plan_1.get(i));
            child_2.set(i, plan_2.get(i));
        }
        
        //Starting from start point, look for elements on that section of the Parent2 that have not been copied.
        //For each of these (say i) look in the Child1 to see what element (say j) has been copied in its place from Parent1.
        //Place i in the position occupied by j in Parent2.
        Node replaceNode_1, replaceNode_2;
        
        for (int i = start; i <= end; i++) {
            replaceNode_1 = plan_1.get(i);
            replaceNode_2 = plan_2.get(i);

            //If the node has already been copied to the child, take no action.
            //Otherwise get the gene that is on the same index on the other parent.
            //Find the position that that gene occupies on this parent.

            if (!child_1.contains(replaceNode_2)) {
                int indexP2j = plan_2.indexOf(replaceNode_1);

                Node nodeP2j= child_1.get(indexP2j);

                if (nodeP2j == null){
                    child_1.set(indexP2j, replaceNode_2);
                } else {
                    //If the place occupied by j in Parent2 has already been filled
                    //in child1, by an element k, put i in the position occupied by k in Parent2.
                    int indexP2k = plan_2.indexOf(nodeP2j);

                    //Verify that the child has no node on this position.
                    if (child_1.get(indexP2k) == null){
                        child_1.set(indexP2k, replaceNode_2);
                    }
                }
            }

            if (!child_2.contains(replaceNode_1)) {
                int indexP1j = plan_1.indexOf(replaceNode_2);

                Node nodeP1j= child_2.get(indexP1j);

                if (nodeP1j == null){
                    child_2.set(indexP1j, replaceNode_1);
                } else {
                    int indexP1k = plan_1.indexOf(nodeP1j);

                    if (child_2.get(indexP1k) == null){
                        child_2.set(indexP1k, replaceNode_1);
                    }
                }
            }
        }
        
        //The remaining elements are placed by verifying which nodes are in Parent2 that don't exist in Child1 and copy them.
        ArrayList<Node> copy_plan_1 = new ArrayList<Node>(plan_1);
        ArrayList<Node> copy_plan_2 = new ArrayList<Node>(plan_2);

        ArrayList<Integer> emptyList_1 = new ArrayList<Integer>();
        ArrayList<Integer> emptyList_2 = new ArrayList<Integer>();

        //For each element in Child1, if it is null, put its index in emptyList1, otherwise remove the element from Parent2.
        for (int i = 0; i < size; i++) {
            Node temp_node1 = child_1.get(i);
            Node temp_node2 = child_2.get(i);

            if (temp_node1 == null) {
                emptyList_1.add(i);
            } else {
                copy_plan_2.remove(temp_node1);
            }

            if (temp_node2 == null) {
                emptyList_2.add(i);
            } else {
                copy_plan_1.remove(temp_node2);
            }
        }
        
        //Put rest nodes from Parent2 in the empty places
        for (int i: emptyList_1) {
            child_1.set(i, copy_plan_2.remove(0));
        }

        for (int i: emptyList_2) {
            child_2.set(i, copy_plan_1.remove(0));
        }

        //Add to the offspring list
        offsprings.add(new ArrayList<Node>(child_1));
        offsprings.add(new ArrayList<Node>(child_2));
        
	}
	
	//
	private ArrayList<Node> Selection_Tournament(int tournament_size, ArrayList<ArrayList<Node>> pop) {

		HashMap<Double, ArrayList<Node>> Tournament_Select = new HashMap<Double, ArrayList<Node>>();

        for(int i = 0; i<tournament_size; i++) {
            int random_Tour = (int) (Math.random() * pop.size());
            Tournament_Select.put(multiOBs.get(random_Tour), pop.get(random_Tour));
        }

        return findBestPlan(Tournament_Select);
    }
	
	//
	private ArrayList<Node> findBestPlan(HashMap<Double, ArrayList<Node>> Tournament_Select) {
		
		Double bestOB = 0.0;
		ArrayList<Node> bestPlan = null;
		
		bestOB = Collections.max(Tournament_Select.keySet());
		
		bestPlan = new ArrayList<Node> (Tournament_Select.get(bestOB));
		
		return bestPlan;
	}
	
	private int[] ToIntegerArray(ArrayList<Node> temp){
		
		int[] planTemp = new int[temp.size()];
		
		int i = 0;
		for(Node node : temp){
			planTemp[i] = node.getID();
		}
		
		return planTemp;
	}
	
	
	

}












