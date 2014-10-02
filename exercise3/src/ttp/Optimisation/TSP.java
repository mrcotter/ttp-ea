package ttp.Optimisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import ttp.Node;
import ttp.TTPInstance;
import ttp.TTPSolution;


public class TSP {
	
	private ArrayList<ArrayList<Node>> multiTours;
	private ArrayList<Node> singleTour = new ArrayList<Node>();
	private ArrayList<ArrayList<Node>> offsprings = new ArrayList<ArrayList<Node>>();

	private TTPInstance instance;
	public double Distance_1 = 0.0, Distance_2 = 0.0;
		
	public TSP(TTPInstance instance) {
		
		this.instance = instance;
        singleTour = instance.tourList;
		
		Distance_1 = totalDistance(singleTour);
	}
	
	public int[] runEA(int popSize, double cross_rate, double mut_rate, int numOfGenerations) {

		ArrayList<Node> temp;
		generatePopulation(popSize, true);
		
		for (int i = 0; i < numOfGenerations; i++) {
            multiTours = EA(multiTours, cross_rate, mut_rate);
		}

        temp = findShortest(multiTours);
		
		int[] shortest = new int[temp.size()];

		int j = 0;
        for (Node aTemp : temp) {
            shortest[j] = aTemp.getID();
            j++;
        }
		
		Distance_2 = totalDistance(temp);

		return shortest;
	}


    private ArrayList<ArrayList<Node>> EA(ArrayList<ArrayList<Node>> pop, double cross_rate, double mut_rate) {

		ArrayList<ArrayList<Node>> nextGeneration = new ArrayList<ArrayList<Node>>(pop.size());
		ArrayList<Node> parent1;
		ArrayList<Node> parent2;
		
		for(int j = 0; j < pop.size(); j++) {
			parent1 = Selection_Tournament(5, pop);
			parent2 = Selection_Tournament(5, pop);
			
			if(Math.random() <= cross_rate){
                HashMap<Integer, Node> lookup_table = new HashMap<Integer, Node>(singleTour.size());

                for (Node node: singleTour)
                    lookup_table.put(node.getID(), node);
				
				Crossover_Edge_Recombination(parent1, parent2, lookup_table);
				Crossover_Edge_Recombination(parent2, parent1, lookup_table);

                nextGeneration.add(offsprings.get(0));
                nextGeneration.add(offsprings.get(1));
				
			} else {
                nextGeneration.add(parent1);
                nextGeneration.add(parent2);
			}
			
			offsprings.clear();		
		}
		
		for(int j = 0; j < pop.size(); j++){
			
			if(Math.random() <= mut_rate) {
				
				Mutation_Inversion(nextGeneration.get(j));
				
			}
		}
			
		return nextGeneration;
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

    // Edge Recombination Crossover
    public void Crossover_Edge_Recombination(ArrayList<Node> cities_1, ArrayList<Node> cities_2, HashMap<Integer, Node> lookup_table) {

        int size = cities_1.size();

        //Create the edge map
        HashMap<Integer, HashSet<Integer>> edgeMap = new HashMap<Integer, HashSet<Integer>>();

        for (int index_1 = 0; index_1 < cities_1.size(); index_1++) {

            int city_1_id = cities_1.get(index_1).getID();

            int index_2 = 0;
            //Get corresponding location in parent 2
            for (int i = 0; i < cities_2.size(); i++) {

                if (cities_2.get(i).getID() == city_1_id) {
                    index_2 = i;
                    break;
                }
            }

            edgeMap.put(city_1_id, getEdges(cities_1, cities_2, index_1, index_2));
        }

        ArrayList<Node> childCity = new ArrayList<Node>(size);
        ArrayList<Integer> unvisited_cityIDs = new ArrayList<Integer>(size);
        for (int i = 1; i <= size; ++i) {
            unvisited_cityIDs.add(i);
        }

        //Pick and store the first city
        Node current_city;
        int parent1_connection_degrees = edgeMap.get(cities_1.get(0).getID()).size();
        int parent2_connection_degrees = edgeMap.get(cities_2.get(0).getID()).size();

        if (parent1_connection_degrees >= parent2_connection_degrees) {
            current_city = cities_1.get(0);
        } else {
            current_city = cities_2.get(0);
        }
        childCity.add(current_city);

        //Remove corresponding entries
        unvisited_cityIDs.remove(unvisited_cityIDs.indexOf(current_city.getID()));
        edgeMap = removeFromEdgeMap(edgeMap, current_city.getID());


        while (!unvisited_cityIDs.isEmpty()) {

            if (!edgeMap.get(current_city.getID()).isEmpty()) {
                current_city = pickNextCity(lookup_table, edgeMap, current_city.getID());

            } else {

                int nextCityId;
                if (unvisited_cityIDs.size() == 1) {
                    nextCityId = unvisited_cityIDs.get(0);
                }
                else {
                    Random rand = new Random();
                    nextCityId = unvisited_cityIDs.get(rand.nextInt(unvisited_cityIDs.size() - 1));
                }

                current_city = lookup_table.get(nextCityId);
            }

            childCity.add(current_city);

            unvisited_cityIDs.remove(unvisited_cityIDs.indexOf(current_city.getID()));
            edgeMap = removeFromEdgeMap(edgeMap, current_city.getID());
        }

        offsprings.add(childCity);
    }


    private HashSet<Integer> getEdges(ArrayList<Node> cities_1, ArrayList<Node> cities_2, int index_1, int index_2) {

        HashSet<Integer> edges = new HashSet<Integer>();
        int size = cities_1.size();

        //Get parent1's edges
        int front = (index_1 + 1) % size;
        int back = (size + index_1 - 1) % size;
        edges.add(cities_1.get(front).getID());
        edges.add(cities_1.get(back).getID());

        //Get parent2's edges
        front = (index_2 + 1) % size;
        back = (size + index_2 - 1) % size;

        //Check if the city is already in parent1's list, if so, negate it
        int[] frontback = {front, back};
        for (int i = 0; i < 2; i++) {

            int id = cities_2.get(frontback[i]).getID();
            if (edges.contains(id)) {
                edges.remove(id);
                edges.add(-1 * id);
            } else
                edges.add(id);
        }

        return edges;
    }


    private HashMap<Integer, HashSet<Integer>> removeFromEdgeMap(HashMap<Integer, HashSet<Integer>> edgeMap, int id) {

        //Remove the given ID from all entries in edge map
        for (Map.Entry<Integer, HashSet<Integer>> edge : edgeMap.entrySet()) {

            HashSet<Integer> connections = edge.getValue();
            connections.remove(id);
            connections.remove(-1 * id);
        }

        return edgeMap;
    }


    private Node pickNextCity(HashMap<Integer, Node> lookup_table, HashMap<Integer, HashSet<Integer>> edgeMap, int id) {

        Node next_city;
        ArrayList<Integer> possible_cities = new ArrayList<Integer>(edgeMap.get(id));

        // 3 Possibilities:
        // 4 cities to consider: all positive.
        // 3 cities to consider: one of them could be negative.
        // 2 cities to consider: both could be negative.

        if (possible_cities.size() == 3) {
            //Pick the negative one if it exists.
            for (int i = 0; i < 3; i++) {

                if (possible_cities.get(i) < 0) {
                    next_city = lookup_table.get(-1 * possible_cities.get(i));
                    return next_city;
                }
            }

        } else if (possible_cities.size() == 1) {
            next_city = lookup_table.get(Math.abs(possible_cities.get(0)));
            return next_city;
        }

        //If 1 of the 2 is negative, pick it for as the next city
        if (possible_cities.size() == 2) {

            int negs = 0;
            int index = 0;

            if (possible_cities.get(0) < 0) {
                ++negs;
                index = 0;
            }

            if (possible_cities.get(1) < 0) {
                ++negs;
                index = 1;
            }

            if (negs == 1) {
                next_city = lookup_table.get(Math.abs(possible_cities.get(index)));
                return next_city;
            }
        }

        //If not picking a negative, or if all are negative, pick the one with the least connections.
        int min_connections = Integer.MAX_VALUE;
        ArrayList<Integer> possible_choices = new ArrayList<Integer>();

        for (Map.Entry<Integer, HashSet<Integer>> edge: edgeMap.entrySet()) {
            // City in edge map listing could be positive or negative.
            if (possible_cities.contains(edge.getKey()) || possible_cities.contains(-1 * edge.getKey())) {

                int num_connections = edge.getValue().size();
                if (num_connections < min_connections) {

                    min_connections = num_connections;
                    possible_choices.clear();
                    possible_choices.add(edge.getKey());

                } else if (edge.getValue().size() == min_connections) {
                    possible_choices.add(edge.getKey());
                }
            }
        }

        //If tie for the least connections, randomly choose.
        if (possible_choices.size() == 1) {
            next_city = lookup_table.get(possible_choices.get(0));
        } else {
            Random rand = new Random();
            next_city = lookup_table.get(Math.abs(possible_choices.get(rand.nextInt(possible_choices.size() - 1))));
        }

        return next_city;
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



















