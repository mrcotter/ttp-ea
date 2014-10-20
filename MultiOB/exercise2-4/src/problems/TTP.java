package jmetal.problems;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationIntSolutionType;
import jmetal.encodings.variable.Int;
import jmetal.encodings.variable.Permutation;
import jmetal.util.JMException;

import java.io.*;

/**
 * Class representing a multi-objective TTP (Traveling Thief Problem) problem.
 */

public class TTP extends Problem {

    public int         numberOfCities_ ;
    public int         numberOfItems_  ;
    public double [][] distanceMatrix_ ;


    /**
     * Creates a new TTP problem instance. It accepts data files from ttp file
     */
    public TTP(String solutionType, String filename) {

    }

    /**
     * Evaluates a solution
     * @param solution The solution to evaluate
     */
    @Override
    public void evaluate(Solution solution) throws JMException {

    }
}
