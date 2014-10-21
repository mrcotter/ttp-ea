package jmetal.operators;

import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.mutation.Mutation;
import jmetal.util.JMException;

import java.util.HashMap;

/**
 * This class implements several variation operators for TTP
 */

public class TTPOperator extends Operator {

    /**
     * Stores the problem to solve
     */
    private Problem problem_;

    /**
     * Stores a reference to the archive in which the non-dominated solutions are
     * inserted
     */
    private SolutionSet archive_;

    private int improvementRounds_;

    /**
     * Stores the mutation operator
     */
    private Operator mutationOperator_;

    /**
     * Stores the number of evaluations_ carried out
     */
    int evaluations_;

    private Double crossoverProbability;

    /**
     * Constructor
     * @param parameters The parameters
     */
    public TTPOperator(HashMap<String, Object> parameters) {
        super(parameters);

        if (parameters.get("problem") != null)
            problem_ = (Problem) parameters.get("problem") ;
        if (parameters.get("improvementRounds") != null)
            improvementRounds_ = (Integer) parameters.get("improvementRounds") ;
        if (parameters.get("mutation") != null)
            mutationOperator_ = (Mutation) parameters.get("mutation") ;
        if (parameters.get("crossProb") != null)
            crossoverProbability = (Double) parameters.get("crossProb");

        evaluations_ = 0;
        archive_ = null;
    }


    /**
     * Executes the operation (including local search and crossover)
     *
     * @param object Object representing a solution
     * @throws JMException
     */
    @Override
    public Object execute(Object object) throws JMException {
        return null;
    }
}
