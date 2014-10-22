package jmetal.operators;

import jmetal.core.Operator;
import jmetal.core.Solution;

import jmetal.encodings.solutionType.PermutationArrayIntSolutionType;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Permutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements several variation operators for TTP
 */

public class TTPOperator extends Operator {

    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(PermutationArrayIntSolutionType.class);

    private Double crossoverProbability_;
    private Double mutationProbability_;

    /**
     * Constructor
     * @param parameters The parameters
     */
    public TTPOperator(HashMap<String, Object> parameters) {
        super(parameters);

        if (parameters.get("crossProb") != null)
            crossoverProbability_ = (Double) parameters.get("crossProb");
        if (parameters.get("mutationProb") != null)
            mutationProbability_ = (Double) parameters.get("mutationProb");
    }

    /**
     * Executes the operation (including crossover and mutation)
     *
     * @param object Object representing a solution
     * @throws JMException
     */
    @Override
    public Object execute(Object object) throws JMException {

        Solution[] parents = (Solution[]) object;

        if (!(VALID_TYPES.contains(parents[0].getType().getClass()) &&
                VALID_TYPES.contains(parents[1].getType().getClass()))) {

            Configuration.logger_.severe("TTPOperator.execute: the solutions " +
                    "are not of the right type. The type should be 'PermutationInt', but " +
                    parents[0].getType() + " and " +
                    parents[1].getType() + " are obtained");
        }

        if (parents.length < 2) {
            Configuration.logger_.severe("TTPOperator.execute: operator needs two " +
                    "parents");
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        }

        // First apply pmx crossover operator only to Variable[0] - TSP
        Solution[] offspring = doPMXCrossover(crossoverProbability_.doubleValue(),
                parents[0],
                parents[1]);

        // Next apply swap mutation operator only to Variable[0] - TSP
        doSwapMutation(mutationProbability_.doubleValue(), offspring[0]);
        doSwapMutation(mutationProbability_.doubleValue(), offspring[1]);

        // Finally apply bit flip only to Variable[1] - Packing Plan
        doBitFlip(offspring[0]);
        doBitFlip(offspring[1]);

        return offspring;
    }

    /**
     * Perform the bit flip with probability 1/n
     * @param solution The solution to mutate
     * @throws JMException
     */
    private void doBitFlip(Solution solution) throws JMException {

        int length = ((ArrayInt) solution.getDecisionVariables()[1]).getLength();
        double probability = 1d / length;

        try {
            // ArrayInt representation
            for (int i = 0; i < length; i++) {
                if (PseudoRandom.randDouble() < probability) {
                    int value = ((ArrayInt) solution.getDecisionVariables()[1]).getValue(i);

                    if (value == 1) {
                        ((ArrayInt) solution.getDecisionVariables()[1]).setValue(i, 0);
                    } else {
                        ((ArrayInt) solution.getDecisionVariables()[1]).setValue(i, 1);
                    }
                }
            }

        } catch (ClassCastException e1) {
            Configuration.logger_.severe("doBitFlip: " +
                    "ClassCastException error" + e1.getMessage());
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".doBitFlip()");
        }
    }

    /**
     * Performs the swap mutation operation
     * @param probability Mutation probability
     * @param solution The solution to mutate
     * @throws JMException
     */
    private void doSwapMutation(double probability, Solution solution) throws JMException {

        int permutation[] ;
        int permutationLength ;
        if (solution.getType().getClass() == PermutationArrayIntSolutionType.class) {

            permutationLength = ((Permutation)solution.getDecisionVariables()[0]).getLength() ;
            permutation = ((Permutation)solution.getDecisionVariables()[0]).vector_ ;

            if (PseudoRandom.randDouble() < probability) {
                int pos1 ;
                int pos2 ;

                pos1 = PseudoRandom.randInt(0,permutationLength-1) ;
                pos2 = PseudoRandom.randInt(0,permutationLength-1) ;

                while (pos1 == pos2) {
                    if (pos1 == (permutationLength - 1))
                        pos2 = PseudoRandom.randInt(0, permutationLength- 2);
                    else
                        pos2 = PseudoRandom.randInt(pos1, permutationLength- 1);
                } // while
                // swap
                int temp = permutation[pos1];
                permutation[pos1] = permutation[pos2];
                permutation[pos2] = temp;
            } // if
        } // if
        else  {
            Configuration.logger_.severe("SwapMutation.doMutation: invalid type. " +
                    ""+ solution.getDecisionVariables()[0].getVariableType());

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".doMutation()") ;
        }
    }

    /**
     * Perform the pmx crossover operation
     *
     * @param probability Crossover probability
     * @param parent1     The first parent
     * @param parent2     The second parent
     * @return An array containing the two offsprings
     */
    private Solution[] doPMXCrossover(double probability, Solution parent1, Solution parent2) {

        Solution[] offspring = new Solution[2];

        offspring[0] = new Solution(parent1);
        offspring[1] = new Solution(parent2);

        int permutationLength;

        permutationLength = ((Permutation) parent1.getDecisionVariables()[0]).getLength();

        int parent1Vector[] = ((Permutation) parent1.getDecisionVariables()[0]).vector_;
        int parent2Vector[] = ((Permutation) parent2.getDecisionVariables()[0]).vector_;
        int offspring1Vector[] = ((Permutation) offspring[0].getDecisionVariables()[0]).vector_;
        int offspring2Vector[] = ((Permutation) offspring[1].getDecisionVariables()[0]).vector_;

        if (PseudoRandom.randDouble() < probability) {
            int cuttingPoint1;
            int cuttingPoint2;

            //      STEP 1: Get two cutting points
            cuttingPoint1 = PseudoRandom.randInt(0, permutationLength - 1);
            cuttingPoint2 = PseudoRandom.randInt(0, permutationLength - 1);
            while (cuttingPoint2 == cuttingPoint1)
                cuttingPoint2 = PseudoRandom.randInt(0, permutationLength - 1);

            if (cuttingPoint1 > cuttingPoint2) {
                int swap;
                swap = cuttingPoint1;
                cuttingPoint1 = cuttingPoint2;
                cuttingPoint2 = swap;
            } // if
            //      STEP 2: Get the subchains to interchange
            int replacement1[] = new int[permutationLength];
            int replacement2[] = new int[permutationLength];
            for (int i = 0; i < permutationLength; i++)
                replacement1[i] = replacement2[i] = -1;

            //      STEP 3: Interchange
            for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
                offspring1Vector[i] = parent2Vector[i];
                offspring2Vector[i] = parent1Vector[i];

                replacement1[parent2Vector[i]] = parent1Vector[i];
                replacement2[parent1Vector[i]] = parent2Vector[i];
            } // for

            //      STEP 4: Repair offsprings
            for (int i = 0; i < permutationLength; i++) {
                if ((i >= cuttingPoint1) && (i <= cuttingPoint2))
                    continue;

                int n1 = parent1Vector[i];
                int m1 = replacement1[n1];

                int n2 = parent2Vector[i];
                int m2 = replacement2[n2];

                while (m1 != -1) {
                    n1 = m1;
                    m1 = replacement1[m1];
                } // while
                while (m2 != -1) {
                    n2 = m2;
                    m2 = replacement2[m2];
                } // while
                offspring1Vector[i] = n1;
                offspring2Vector[i] = n2;
            } // for
        } // if

        return offspring;
    }
}
