package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationArrayIntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Permutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a mutation for TTP.
 * NOTE: the inversion mutation is applied to the first encodings.variable of the solutions,
 * the bit flip with probability operation is applied to the second encodings.variable of the solutions.
 * The solution type of the solution must be PermutationArrayInt.
 */
public class Mutation_TTP extends Mutation {

    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(PermutationArrayIntSolutionType.class);

    private Double mutationProbability_ = null;

    /**
     * Constructor
     * @param parameters The parameters
     */
    public Mutation_TTP(HashMap<String, Object> parameters) {
        super(parameters);

        if (parameters.get("probability") != null)
            mutationProbability_ = (Double) parameters.get("probability");
    }

    /**
     * Performs the operation
     * @param probability Mutation probability
     * @param solution The solution to mutate
     * @throws JMException
     */
    public void doMutation(double probability, Solution solution) throws JMException {
        // Inversion Mutation
        if (PseudoRandom.randDouble() < probability) {
            int permutation[];
            int permutationLength;

            permutationLength = ((Permutation)solution.getDecisionVariables()[0]).getLength();
            permutation = ((Permutation)solution.getDecisionVariables()[0]).vector_;

            int pos1 = PseudoRandom.randInt(0,permutationLength-1);
            int pos2 = PseudoRandom.randInt(0,permutationLength-1);

            while (pos1 == pos2) {
                if (pos1 == (permutationLength - 1))
                    pos2 = PseudoRandom.randInt(0, permutationLength - 2);
                else
                    pos2 = PseudoRandom.randInt(pos1, permutationLength - 1);
            }

            int lower_index = Math.min(pos1, pos2);
            int bigger_index = Math.max(pos1, pos2);

            //Do invert the sub section among these two indices
            while (lower_index <= bigger_index) {
                int temp = permutation[lower_index];
                permutation[lower_index] = permutation[bigger_index];
                permutation[bigger_index] = temp;

                lower_index++;
                bigger_index--;
            }
        }

        // Bit flip with probability 1/n
        if (PseudoRandom.randDouble() < probability) {
            int length = ((ArrayInt) solution.getDecisionVariables()[1]).getLength();
            double nProb = 1d / length;

            // ArrayInt representation
            for (int i = 0; i < length; i++) {
                if (PseudoRandom.randDouble() < nProb) {
                    int value = ((ArrayInt) solution.getDecisionVariables()[1]).getValue(i);

                    if (value == 1) {
                        ((ArrayInt) solution.getDecisionVariables()[1]).setValue(i, 0);
                    } else {
                        ((ArrayInt) solution.getDecisionVariables()[1]).setValue(i, 1);
                    }
                }
            }
        }
    }

    /**
     * Executes the operation
     * @param object An object containing the solution to mutate
     * @return an object containing the mutated solution
     * @throws JMException
     */
    @Override
    public Object execute(Object object) throws JMException {
        Solution solution = (Solution)object;

        if (!VALID_TYPES.contains(solution.getType().getClass())) {
            Configuration.logger_.severe("Mutation_TTP.execute: the solution " +
                    "is not of the right type. The type should be PermutationArrayInt, but "
                    + solution.getType() + " is obtained");

            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        }

        this.doMutation(mutationProbability_, solution);
        return solution;
    }

}
