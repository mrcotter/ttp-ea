package jmetal.metaheuristics.nsgaII;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.mTSP;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * Class to configure and execute the NSGA-II algorithm. It is aimed
 * at solving the TTP problem.
 */

public class NSGAII_TTP_main {
    public static Logger      logger_ ;      // Logger object
    public static FileHandler fileHandler_ ; // FileHandler object

    /**
     * @param args Command line arguments.
     * @throws JMException
     * @throws SecurityException
     * @throws IOException
     * @throws ClassNotFoundException
     * Usage:
     *      - jmetal.metaheuristics.nsgaII.NSGAII_TTP_main
     */

    public static void main(String [] args) throws
                                    JMException,
                                    SecurityException,
                                    IOException,
                                    ClassNotFoundException {

    }
}
