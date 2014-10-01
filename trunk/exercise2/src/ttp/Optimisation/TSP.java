package ttp.Optimisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import ttp.TTPInstance;
import ttp.TTPSolution;


public class TSP {
	
	private ArrayList<ArrayList<Integer>> MultiTours;
	private ArrayList<Integer> SingleTour = new ArrayList<Integer>();
	private ArrayList<ArrayList<Integer>> offsprings = new ArrayList<ArrayList<Integer>>();
	private TTPInstance instance;
		
	public TSP(TTPInstance instance, TTPSolution solution){
		
		this.instance = instance;
		 
		for(int i = 0; i<solution.tspTour.length - 1; i++)
		{
			SingleTour.add(solution.tspTour[i]);
		}
			
	}
	
	
	private ArrayList<ArrayList<Integer>> EA(ArrayList<ArrayList<Integer>> pop, double cross_rate, double mut_rate)
	{
		ArrayList<ArrayList<Integer>> NextGeneration = new ArrayList<ArrayList<Integer>>(pop.size());
		ArrayList<Integer> parent1;
		ArrayList<Integer> parent2;
		
		for(int j = 0; j < pop.size(); j++)
		{
			parent1 = Selection_Tournament(5, pop);
			parent2 = Selection_Tournament(5, pop);
			
			if(Math.random() <= cross_rate){
				HashMap<Integer, Integer> lookup_table = new HashMap<Integer, Integer>(SingleTour.size());
				ArrayList<ArrayList<Integer>> tours = pop;
				
				for(int i : tours.get(0))
				{
					lookup_table.put(i, i);
				}
				
				Crossover_Edge_Recombination(parent1, parent2, lookup_table);
				Crossover_Edge_Recombination(parent2, parent1, lookup_table);
				
				NextGeneration.add(offsprings.get(0));
				NextGeneration.add(offsprings.get(1));
				
			}else{
				NextGeneration.add(parent1);
				NextGeneration.add(parent2);		
			}
			
			offsprings.clear();		
		}
		
		for(int j = 0; j < pop.size(); j++){
			
			if(Math.random() <= mut_rate) {
				
				Mutation_Inversion(NextGeneration.get(j));
				
			}
		}
			
		return NextGeneration;
	}
	
	
	private void Population(int PopSize, boolean ifInitial ){
		
		MultiTours = new ArrayList<ArrayList<Integer>>(PopSize);
		
		if(ifInitial == true){
			
			for(int i = 0; i < PopSize; i++)
			{
				MultiTours.add(SingleTour);
				CreateARandomTour();				
			}
					
		}
				
	}
	
	
	private void CreateARandomTour(){
		
		Collections.shuffle(SingleTour);
	}
	
	
	private void Mutation_Inversion(ArrayList<Integer> tour){
		
		ArrayList<Integer> Random_Int = new ArrayList<Integer>();
		
		for (int j = 0; j < tour.size(); j++)
		{
			Random_Int.add(j);
		}
		
		Collections.shuffle(Random_Int);
		
		int RandomPos_1 = Random_Int.get(0);
		long seed = System.nanoTime();
		Random rand = new Random(seed);
		int RandomPos_2 = Random_Int.get(rand.nextInt(tour.size()-1) + 1);
		
		if(RandomPos_1 > RandomPos_2)
		{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			int temp_num = RandomPos_1; 
			for(int j = RandomPos_2; j <= RandomPos_1; j++)
			{
				temp.add(tour.get(j));
			}
			
			for(int j = 0; j < temp.size(); j++)
			{
				tour.set(temp_num, temp.get(j));
				temp_num--;
			}
		}
		
		if(RandomPos_1 < RandomPos_2)
		{
			ArrayList<Integer> temp = new ArrayList<Integer>();
			int temp_num = RandomPos_2; 
			for(int j = RandomPos_1; j <= RandomPos_2; j++)
			{
				temp.add(tour.get(j));
			}
			
			for(int j = 0; j < temp.size(); j++)
			{
				tour.set(temp_num, temp.get(j));
				temp_num--;
			}
		}

	}
	
	private void Crossover_Edge_Recombination(ArrayList<Integer> P1, ArrayList<Integer> P2, HashMap<Integer, Integer> LookUp_Table){
		
		//Create the edge map
        HashMap<Integer, HashSet<Integer>> edgeMap = new HashMap<Integer, HashSet<Integer>>();
        
        for (int index_1 = 0; index_1 < P1.size(); index_1++) {

            int city_1_id = P1.get(index_1);

            int index_2 = 0;
            //Get corresponding location in parent 2
            for (int i = 0; i < P2.size(); i++) {

                if (P2.get(i) == city_1_id) {
                    index_2 = i;
                    break;
                }
            }

            edgeMap.put(city_1_id, getEdges(P1, P2, index_1, index_2));
        }
        
        ArrayList<Integer> ChildCity = new ArrayList<Integer>(P1.size());
        ArrayList<Integer> unvisited_cityIDs = new ArrayList<Integer>(P1.size());
        for (int i = 1; i <= P1.size(); ++i) {
            unvisited_cityIDs.add(i);
        }
        
        //Pick and store the first city
        int current_city;
        int parent1_connection_degrees = edgeMap.get(P1.get(0)).size();
        int parent2_connection_degrees = edgeMap.get(P2.get(0)).size();
        
        if (parent1_connection_degrees >= parent2_connection_degrees) {
            current_city = P1.get(0);
        } else {
            current_city = P2.get(0);
        }
        ChildCity.add(current_city);
        
        //Remove corresponding entries
        unvisited_cityIDs.remove(unvisited_cityIDs.indexOf(current_city));
        edgeMap = removeFromEdgeMap(edgeMap, current_city);
        
        while (!unvisited_cityIDs.isEmpty()){
        	
        	if (!edgeMap.get(current_city).isEmpty()) {
                current_city = pickNextCity(LookUp_Table, edgeMap, current_city);

            }else{

                int nextCityId;
                if (unvisited_cityIDs.size() == 1) {
                    nextCityId = unvisited_cityIDs.get(0);
                }
                else {
                    Random rand = new Random();
                    nextCityId = unvisited_cityIDs.get(rand.nextInt(unvisited_cityIDs.size() - 1));
                }

                current_city = LookUp_Table.get(nextCityId);
            }
        	
        	ChildCity.add(current_city);

            unvisited_cityIDs.remove(unvisited_cityIDs.indexOf(current_city));
            edgeMap = removeFromEdgeMap(edgeMap, current_city);
        	
        }

		
        offsprings.add(ChildCity);
	}
	
	
	private ArrayList<Integer> Selection_Tournament(int tournament_size, ArrayList<ArrayList<Integer>> pop){
		
		ArrayList<ArrayList<Integer>> Tournament_Select = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i<tournament_size; i++)
		{
			int random_Tour = (int) (Math.random() * pop.size());
			Tournament_Select.add(pop.get(random_Tour));
		}
		
		ArrayList<Integer> Shorest = FindShorest(Tournament_Select);
		
		return Shorest;
	}
	
	
	private HashSet<Integer> getEdges(ArrayList<Integer> P1, ArrayList<Integer> P2, int index_1, int index_2)
	{
		HashSet<Integer> edges = new HashSet<Integer>();
		int size = P1.size();
		
		//Get parent1's edges
        int front = (index_1 + 1) % size;
        int back = (size + index_1 - 1) % size;
        edges.add(P1.get(front));
        edges.add(P1.get(back));
        
        //Get parent2's edges
        front = (index_2 + 1) % size;
        back = (size + index_2 - 1) % size;
        
      //Check if the city is already in parent1's list, if so, negate it
        int[] frontback = {front, back};
        for (int i = 0; i < 2; i++) {

            int id = P2.get(frontback[i]);
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
	
	
	private int pickNextCity(HashMap<Integer, Integer> lookup_table, HashMap<Integer, HashSet<Integer>> edgeMap, int id) {
		
		int Next_City;
		ArrayList<Integer> possible_cities = new ArrayList<Integer>(edgeMap.get(id));
		
		// 3 Possibilities:
        // 4 cities to consider: all positive.
        // 3 cities to consider: one of them could be negative.
        // 2 cities to consider: both could be negative.
		
		if (possible_cities.size() == 3) {
            //Pick the negative one if it exists.
            for (int i = 0; i < 3; i++) {

                if (possible_cities.get(i) < 0) {
                    Next_City = lookup_table.get(-1 * possible_cities.get(i));
                    return Next_City;
                }
            }

        } else if (possible_cities.size() == 1) {
            Next_City = lookup_table.get(Math.abs(possible_cities.get(0)));
            return Next_City;
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
                Next_City = lookup_table.get(Math.abs(possible_cities.get(index)));
                return Next_City;
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
             Next_City = lookup_table.get(possible_choices.get(0));
         } else {
             Random rand = new Random();
             Next_City = lookup_table.get(Math.abs(possible_choices.get(rand.nextInt(possible_choices.size() - 1))));
         }

         return Next_City;
           
    }
	
	
	private ArrayList<Integer> FindShorest(ArrayList<ArrayList<Integer>> tour){
		
		ArrayList<Integer> Shorest = tour.get(0);
		
		for(int i = 0; i < tour.size(); i++)
		{
			if(totalDistance(Shorest) > totalDistance(tour.get(i)))
			{
				Shorest = tour.get(i);
			}
		}
		
		
		return Shorest;
	}
	
	
	private long totalDistance(ArrayList<Integer> tour)
	{
		long Distance = 0;
		
		for(int i = 0; i < tour.size() - 1; i++)
		{
			//h: next tour city index
			int h= (i+1)%(tour.size()-1);
			Distance = (long)Math.ceil(instance.distances(tour.get(i),tour.get(h)));
		}
		
		
		return Distance;
	}

		
	
	
	

}



















