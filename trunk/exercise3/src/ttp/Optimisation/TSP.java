package ttp.Optimisation;

import java.util.*;

import ttp.Node;
import ttp.TTPInstance;
import ttp.TTPSolution;


public class TSP {
	
	private ArrayList<ArrayList<Node>> multiTours;
	private ArrayList<Node> singleTour;

	private ArrayList<ArrayList<Node>> offsprings = new ArrayList<ArrayList<Node>>();

	private TTPInstance instance;
	public double Distance_1 = 0.0, Distance_2 = 0.0;


	public TSP(TTPInstance instance) {
		
		this.instance = instance;
        singleTour = instance.tourList;

		//Distance_1 = totalDistance(singleTour);
	}

	
	public int[] runGA(int popSize, int generations, double cross_rate, double mut_rate) {

		ArrayList<Node> temp, best;
        double tempDistance, bestDistance;
		generatePopulation(popSize, true);

        //System.out.println(multiTours.size());
        //System.out.println("initial tour: " + totalDistance(singleTour));

        multiTours = GA(multiTours, cross_rate, mut_rate);
        best = findShortest(multiTours);
        bestDistance = totalDistance(best);


        //System.out.println(multiTours.size());
		
		for (int i = 1; i < generations; i++) {

            //System.out.println("generation: " + i);
            multiTours = GA(multiTours, cross_rate, mut_rate);
            temp = findShortest(multiTours);
            tempDistance = totalDistance(temp);

            if (tempDistance < bestDistance) {
                best = temp;
            }

            //System.out.println(multiTours.size() + "\n");
		}

        //System.out.println("best tour: " + totalDistance(best));

        /** ------ Need Repair Operator ---------- **/

		int[] shortest = new int[best.size()];

		int j = 0;
        for (Node node : best) {
            shortest[j] = node.getID();
            j++;
        }
		
		//Distance_2 = totalDistance(temp);

		return shortest;
	}

    private ArrayList<ArrayList<Node>> GA(ArrayList<ArrayList<Node>> pop, double cross_rate, double mut_rate) {

        //System.out.println(pop.size());

        ArrayList<ArrayList<Node>> nextGeneration = new ArrayList<ArrayList<Node>>(pop.size());
		ArrayList<Node> parent1;
		ArrayList<Node> parent2;

        int popSize = pop.size();

        //System.out.println(nextGeneration.size());
		
		for(int j = 0; j < popSize; j = j + 2) {
			parent1 = Selection_Tournament(5, pop);
			parent2 = Selection_Tournament(5, pop);
			
			if(Math.random() <= cross_rate){
				
				Crossover_Crossover_PMX(parent1, parent2);
				Crossover_Crossover_PMX(parent2, parent1);

                nextGeneration.add(offsprings.get(0));
                nextGeneration.add(offsprings.get(1));
				
			} else {
                nextGeneration.add(parent1);
                nextGeneration.add(parent2);
			}
			
			offsprings.clear();		
		}

        //System.out.println(nextGeneration.size());

        for(int j = 0; j < popSize; j++){
			
			if(Math.random() <= mut_rate) {
				
				Mutation_Inversion(nextGeneration.get(j));
				
			}
		}

        //System.out.println(nextGeneration.size());

		return nextGeneration;
	}

    private void Crossover_Crossover_PMX(ArrayList<Node> tour_1, ArrayList<Node> tour_2) {

        int size = tour_1.size();

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
            child_1.set(i, tour_1.get(i));
            child_2.set(i, tour_2.get(i));
        }

        //Starting from start point, look for elements on that section of the Parent2 that have not been copied.
        //For each of these (say i) look in the Child1 to see what element (say j) has been copied in its place from Parent1.
        //Place i in the position occupied by j in Parent2.
        Node replaceNode_1, replaceNode_2;

        for (int i = start; i <= end; i++) {
            replaceNode_1 = tour_1.get(i);
            replaceNode_2 = tour_2.get(i);

            //If the node has already been copied to the child, take no action.
            //Otherwise get the gene that is on the same index on the other parent.
            //Find the position that that gene occupies on this parent.

            if (!child_1.contains(replaceNode_2)) {
                int indexP2j = tour_2.indexOf(replaceNode_1);

                Node nodeP2j= child_1.get(indexP2j);

                if (nodeP2j == null){
                    child_1.set(indexP2j, replaceNode_2);
                } else {
                    //If the place occupied by j in Parent2 has already been filled
                    //in child1, by an element k, put i in the position occupied by k in Parent2.
                    int indexP2k = tour_2.indexOf(nodeP2j);

                    //Verify that the child has no node on this position.
                    if (child_1.get(indexP2k) == null){
                        child_1.set(indexP2k, replaceNode_2);
                    }
                }
            }

            if (!child_2.contains(replaceNode_1)) {
                int indexP1j = tour_1.indexOf(replaceNode_2);

                Node nodeP1j= child_2.get(indexP1j);

                if (nodeP1j == null){
                    child_2.set(indexP1j, replaceNode_1);
                } else {
                    int indexP1k = tour_1.indexOf(nodeP1j);

                    if (child_2.get(indexP1k) == null){
                        child_2.set(indexP1k, replaceNode_1);
                    }
                }
            }
        }

        //The remaining elements are placed by verifying which nodes are in Parent2 that don't exist in Child1 and copy them.
        ArrayList<Node> copy_tour_1 = new ArrayList<Node>(tour_1);
        ArrayList<Node> copy_tour_2 = new ArrayList<Node>(tour_2);

        ArrayList<Integer> emptyList_1 = new ArrayList<Integer>();
        ArrayList<Integer> emptyList_2 = new ArrayList<Integer>();

        //For each element in Child1, if it is null, put its index in emptyList1, otherwise remove the element from Parent2.
        for (int i = 0; i < size; i++) {
            Node temp_node1 = child_1.get(i);
            Node temp_node2 = child_2.get(i);

            if (temp_node1 == null) {
                emptyList_1.add(i);
            } else {
                copy_tour_2.remove(temp_node1);
            }

            if (temp_node2 == null) {
                emptyList_2.add(i);
            } else {
                copy_tour_1.remove(temp_node2);
            }
        }

        //Put rest nodes from Parent2 in the empty places
        for (int i: emptyList_1) {
            child_1.set(i, copy_tour_2.remove(0));
        }

        for (int i: emptyList_2) {
            child_2.set(i, copy_tour_1.remove(0));
        }

        //Add to the offspring list
        offsprings.add(new ArrayList<Node>(child_1));
        offsprings.add(new ArrayList<Node>(child_2));
    }


    private void generatePopulation(int popSize, boolean ifInitial){
		
		multiTours = new ArrayList<ArrayList<Node>>(popSize);
		
		if(ifInitial) {
			
			for(int i = 0; i < popSize; i++) {
                multiTours.add(singleTour);
                Collections.shuffle(singleTour);
			}
					
		}
	}


    private ArrayList<Node> Selection_Tournament(int tournament_size, ArrayList<ArrayList<Node>> pop) {

        ArrayList<ArrayList<Node>> Tournament_Select = new ArrayList<ArrayList<Node>>();

        for(int i = 0; i<tournament_size; i++) {
            int random_Tour = (int) (Math.random() * pop.size());
            Tournament_Select.add(pop.get(random_Tour));
        }

        return findShortest(Tournament_Select);
    }
	
	
	private void Mutation_Inversion(ArrayList<Node> tour){

        int size = tour.size();
        int index_1, index_2;

        do {
            Random rand_index = new Random();
            index_1 = rand_index.nextInt(size);
            index_2 = rand_index.nextInt(size);
        } while (index_1 == index_2);

        int lower_index = Math.min(index_1, index_2);
        int bigger_index = Math.max(index_1, index_2);

        //Do invert the sub section among these two indices
        while (lower_index <= bigger_index) {
            Collections.swap(tour, lower_index, bigger_index);
            lower_index++;
            bigger_index--;
        }

	}


	
	
	private ArrayList<Node> findShortest(ArrayList<ArrayList<Node>> tours){
		
		ArrayList<Node> shortest = tours.get(0);

        for (ArrayList<Node> aTour : tours) {
            if (totalDistance(shortest) > totalDistance(aTour)) {
                shortest = aTour;
            }
        }

		return shortest;
	}
	
	
	public double totalDistance(ArrayList<Node> tour) {
		double Distance = 0.0;
		
		for (int i = 0; i < tour.size(); i++)
		{
			//h: next tour city index
			int h = (i+1)%(tour.size()-1);
			Distance = Distance + Math.ceil(instance.distances(tour.get(i).getID(), tour.get(h).getID()));
		}

		return Distance;
	}

}



















