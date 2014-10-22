package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationArrayIntSolutionType;
import jmetal.encodings.variable.ArrayInt;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a bit flip with probability operation for TTP.
 * NOTE: the operator is applied to the second encodings.variable of the solutions.
 * The solution type of the solution must be PermutationArrayInt.
 */
public class BitFlipWithProbMutation_TTP extends Mutation {
    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(PermutationArrayIntSolutionType.class);

    private Double mutationProbability_ = null;

    /**
     * Constructor
     * @param parameters The parameters
     */
    public BitFlipWithProbMutation_TTP(HashMap<String, Object> parameters) {
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
        int length = ((ArrayInt) solution.getDecisionVariables()[1]).getLength();
        double nProb = 1d / length;

        if (PseudoRandom.randDouble() < probability) {
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
            Configuration.logger_.severe("BitFlipWithProbMutation_TTP.execute: the solution " +
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
