package jmetal.operators;

import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.solutionType.PermutationIntSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.operators.mutation.Mutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.OverallConstraintViolationComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements several variation operators for TTP
 */

public class TTPOperator extends Operator {

    /**
     * Valid solution types to apply this operator
     */
    private static final List VALID_TYPES = Arrays.asList(PermutationIntSolutionType.class);

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
     * Stores comparators for dealing with constraints and dominance checking,
     * respectively.
     */
    private Comparator constraintComparator_ ;
    private Comparator dominanceComparator_ ;

    /**
     * Stores the mutation operator
     */
    private Operator mutationOperator_;

    /**
     * Stores the number of evaluations_ carried out
     */
    int evaluations_;

    private Double crossoverProbability_;

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
            crossoverProbability_ = (Double) parameters.get("crossProb");

        evaluations_ = 0;
        archive_ = null;
        dominanceComparator_  = new DominanceComparator();
        constraintComparator_ = new OverallConstraintViolationComparator();
    }

    /**
     * Executes the operation (including local search and crossover)
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

        Double crossoverProbability = (Double) getParameter("probability");

        if (parents.length < 2) {
            Configuration.logger_.severe("TTPOperator.execute: operator needs two " +
                    "parents");
            Class cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        }

        // First apply crossover operation only to Variable[0] - TSP
        Solution[] partialOffspring = doCrossover(crossoverProbability.doubleValue(),
                parents[0],
                parents[1]);

        // Next apply mutation local search to Variable[0] - TSP and Variable[1] - Packing Plan
        Solution[] offspring = new Solution[2];
        offspring[0] = doMutationLocalSearch(partialOffspring[0]);
        offspring[1] = doMutationLocalSearch(partialOffspring[1]);

        return offspring;
    }

    private Solution doMutationLocalSearch(Solution solution) throws JMException {

        int i = 0;
        int best = 0;
        evaluations_ = 0;

        int rounds = improvementRounds_;
        archive_ = (SolutionSet) getParameter("archive");

        if (rounds <= 0)
            return new Solution(solution);

        do {
            i++;
            Solution mutatedSolution = new Solution(solution);
            mutationOperator_.execute(mutatedSolution);

            // Evaluate the getNumberOfConstraints
            if (problem_.getNumberOfConstraints() > 0) {
                problem_.evaluateConstraints(mutatedSolution);
                best = constraintComparator_.compare(mutatedSolution,solution);

                if (best == 0) { //none of then is better that the other one
                    problem_.evaluate(mutatedSolution);
                    evaluations_++;
                    best = dominanceComparator_.compare(mutatedSolution,solution);
                }
                else if (best == -1) { //mutatedSolution is best
                    problem_.evaluate(mutatedSolution);
                    evaluations_++;
                }
            }
            else {
                problem_.evaluate(mutatedSolution);
                evaluations_++;
                best = dominanceComparator_.compare(mutatedSolution,solution);
            }

            if (best == -1) // This is: Mutated is best
                solution = mutatedSolution;
            else if (best == 1) // This is: Original is best
                //delete mutatedSolution
                ;
            else { // This is mutatedSolution and original are non-dominated
                //this.archive_.addIndividual(new Solution(solution));
                //solution = mutatedSolution;
                if (archive_ != null)
                    archive_.add(mutatedSolution);
            }
        }

        while (i < rounds);
        return new Solution(solution);
    }


    private Solution[] doCrossover(double probability, Solution parent1, Solution parent2) {

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
