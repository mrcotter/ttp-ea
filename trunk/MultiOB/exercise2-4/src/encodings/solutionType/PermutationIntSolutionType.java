package jmetal.encodings.solutionType;

import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.variable.Int;
import jmetal.encodings.variable.Permutation;


/**
 * Class representing a solution type including two variables: a permutation
 * and a int.
 */

public class PermutationIntSolutionType extends SolutionType {

    private final int intVariables_ ;
    private final int permutationVariables_ ;

    /**
     * Constructor
     * @param problem  Problem to solve
     * @param permutationVariables Number of integer variables
     * @param intVariables Number of real variables
     */
    public PermutationIntSolutionType(Problem problem, int permutationVariables, int intVariables) {
        super(problem) ;
        permutationVariables_ = permutationVariables ;
        intVariables_ = intVariables ;
    } // Constructor

    /**
     * Creates the variables of the solution
     * @throws ClassNotFoundException
     */
    @Override
    public Variable[] createVariables() throws ClassNotFoundException {
        Variable [] variables = new Variable[problem_.getNumberOfVariables()];

        for (int var = 0; var < permutationVariables_; var++)
            variables[var] = new Permutation(problem_.getLength(var));

        for (int var = permutationVariables_; var < (permutationVariables_ + intVariables_); var++)
            variables[var] = new Int((int)problem_.getLowerLimit(var),
                    (int)problem_.getUpperLimit(var));

        return variables;
    }   // createVariables
}   // PermutationIntSolutionType
