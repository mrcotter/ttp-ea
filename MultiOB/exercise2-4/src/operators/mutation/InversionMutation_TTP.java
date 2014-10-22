package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationArrayIntSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a inversion mutation for TTP.
 * NOTE: the operator is applied to the first encodings.variable of the solutions.
 * The solution type of the solution must be PermutationArrayInt.
 */

public class InversionMutation_TTP extends Mutation {

    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(PermutationArrayIntSolutionType.class);

    private Double mutationProbability_ = null;

    /**
     * Constructor
     * @param parameters The parameters
     */
    public InversionMutation_TTP(HashMap<String, Object> parameters) {
        super(parameters) ;

        if (parameters.get("probability") != null)
            mutationProbability_ = (Double) parameters.get("probability");
    }

    /**
     * Performs the operation
     * @param probability Mutation probability
     * @param solution The solution to mutate
     */
    public void doMutation(double probability, Solution solution){
        int permutation[];
        int permutationLength;

        permutationLength = ((Permutation)solution.getDecisionVariables()[0]).getLength();
        permutation = ((Permutation)solution.getDecisionVariables()[0]).vector_;

        if (PseudoRandom.randDouble() < probability) {
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
            Configuration.logger_.severe("InversionMutation_TTP.execute: the solution " +
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
