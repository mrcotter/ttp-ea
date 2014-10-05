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
	
	public ArrayList<ArrayList<Node>> multiPlans = new ArrayList<ArrayList<Node>>();;
	public ArrayList<Double> multiOBs = new ArrayList<Double>();
	private ArrayList<ArrayList<Node>> offsprings = new ArrayList<ArrayList<Node>>();
	private ArrayList<TTPSolution> multiSols = new ArrayList<TTPSolution>();
	private int[] tour;
	private TTPInstance instance;
	
	public CrossOverTTP(TTPInstance instance, int[] tour) {
		
			this.tour = (int[])DeepCopy.copy(tour);
			this.instance = instance;
	}
		
	
	public TTPSolution RunGA(int generations, double cross_rate, int MaxRunTime){
		
		TTPSolution bestSolution = null;
		
		ArrayList<Double> fixedOBs = new ArrayList<Double>(multiOBs);
				
		double bestObjective = Collections.max(fixedOBs);
		        
        bestSolution = multiSols.get(multiOBs.indexOf(bestObjective));
        
        //ttp.Utils.Utils.startTiming();
        
        long startingTimeForRuntimeLimit = System.currentTimeMillis()-200;
        
        for(int i = 0; i < generations; i++){

        	if (i%10==0 && (System.currentTimeMillis()-startingTimeForRuntimeLimit)>=MaxRunTime){
            		break;
            	}              
        	
        	GA_Plan(multiPlans, cross_rate);
        	
        	for(int j = 0; j < multiPlans.size(); j++){       		
        		TTPSolution newSolution = new TTPSolution(tour, ToIntegerArray(multiPlans.get(j)));
                instance.evaluate(newSolution);
                
                if (newSolution.ob >= bestObjective && newSolution.wend >=0 ) {
                	bestSolution = newSolution;
                	bestObjective = newSolution.ob;
                }
        		
        	}
        }
        
        long duration = ttp.Utils.Utils.stopTiming();
        bestSolution.computationTime = duration;
		
		
		return bestSolution;
	}
	
	//
	private void GA_Plan(ArrayList<ArrayList<Node>> pop, double cross_rate){
		
		ArrayList<ArrayList<Node>> nextGeneration = new ArrayList<ArrayList<Node>>(pop.size());
		ArrayList<Node> parent1;
		ArrayList<Node> parent2;
		
		int popSize = pop.size();
		
		for(int j = 0; j < popSize/2; j++){
						 
			parent1 = Selection_Tournament(5, pop);
			parent2 = Selection_Tournament(5, pop);
			
			
			if(Math.random() <= cross_rate){
				
				Crossover_Order(parent1, parent2);
				Crossover_Order(parent2, parent1);

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
		
		sol.generatePlanList(sol.packingPlan);
		multiPlans.add(sol.packList);
		multiOBs.add(sol.ob);
		multiSols.add(sol);	
	}
	
	//
	private void Crossover_Order(ArrayList<Node> plan_1, ArrayList<Node> plan_2){
		
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

        //Copy the part in between the start and end to the children
      //Copy the section from the parents to the children
        for (int i = start; i <= end; i++) {
            child_1.set(i, plan_1.get(i));
            child_2.set(i, plan_2.get(i));
        }
        
        //Do order procedure
        int current_index;
        Node current_node_in_plan1, current_node_in_plan2;

        for (int i = 0; i < size; i++) {
            current_index = (end + i) % size;
            //System.out.print(current_index + " ");
            current_node_in_plan1 = plan_1.get(current_index);
            current_node_in_plan2 = plan_2.get(current_index);

            child_1.set(i, current_node_in_plan2);
            
            child_2.set(i, current_node_in_plan1);
            
        }
        
        //Rotate the list so that the elements are in the right place
        Collections.rotate(child_1, start);
        Collections.rotate(child_2, start);

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
		
		ArrayList<Node> bestPlan = new ArrayList<Node>();
		
		Double bestOB = Collections.max(Tournament_Select.keySet());
		
		bestPlan = Tournament_Select.get(bestOB);
		
		return bestPlan;
	}
	
	//
	private int[] ToIntegerArray(ArrayList<Node> temp){
		
		int[] planTemp = new int[temp.size()];
		
		int i = 0;
		for(Node node : temp){
			planTemp[i] = node.getID();
			i++;
		}
		
		return planTemp;
	}
	
	
	

}












