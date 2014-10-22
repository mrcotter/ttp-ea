package jmetal.encodings.solutionType;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.ArrayInt;
import jmetal.encodings.variable.Permutation;


/**
 * Class representing a solution type including two variables: a permutation
 * and a int.
 */

public class PermutationArrayIntSolutionType extends SolutionType {

    private final int arrayIntVariables_ ;
    private final int permutationVariables_ ;

    /**
     * Constructor
     * @param problem  Problem to solve
     * @param permutationVariables Number of integer variables
     * @param intVariables Number of real variables
     */
    public PermutationArrayIntSolutionType(Problem problem, int permutationVariables, int intVariables) {
        super(problem) ;
        permutationVariables_ = permutationVariables ;
        arrayIntVariables_ = intVariables ;
    } // Constructor

    /**
     * Creates the variables of the solution
     * @throws ClassNotFoundException
     */
    @Override
    public Variable[] createVariables() throws ClassNotFoundException {
        Variable [] variables = new Variable[problem_.getNumberOfVariables()];

        for (int var = 0; var < permutationVariables_; var++) {
            //System.out.println("v0: " + var);
            variables[var] = new Permutation(problem_.getLength(var));
        }

        for (int var = permutationVariables_; var < (permutationVariables_ + arrayIntVariables_); var++) {
            //System.out.println("v1: " + var);
            //System.out.println(problem_.getLength(var));
            variables[var] = new ArrayInt(problem_.getLength(var), problem_);
        }

        return variables;
    }   // createVariables
}   // PermutationIntSolutionType
