package jmetal.metaheuristics.nsgaII;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.TTP;
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
     *      - jmetal.metaheuristics.nsgaII.NSGAII_TTP_main ttp_fileName
     */

    public static void main(String [] args) throws
                                    JMException,
                                    SecurityException,
                                    IOException,
                                    ClassNotFoundException {
        Problem   problem = null;   // The problem to solve
        Algorithm algorithm ;       // The algorithm to use
        Operator  crossover ;       // Crossover operator
        Operator  mutation  ;       // Mutation operator
        Operator  selection ;       // Selection operator

        HashMap  parameters ; // Operator parameters

        QualityIndicator indicators ; // Object to get quality indicators

        // Logger object and file to store log messages
        logger_      = Configuration.logger_ ;
        fileHandler_ = new FileHandler("NSGAII_main.log");
        logger_.addHandler(fileHandler_) ;

        indicators = null ;

        if (args.length == 0) {
            problem = new TTP("PermutationArrayInt", "a280_n279_bounded-strongly-corr_01.ttp");
        }
        else if (args.length == 1) {
            problem = new TTP("PermutationArrayInt", args[0]);
        } else {
            System.err.println("Usage: - jmetal.metaheuristics.nsgaII.NSGAII_TTP_main ttp_fileName");
            System.exit(1);
        }

        algorithm = new NSGAII(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize",10);
        algorithm.setInputParameter("maxEvaluations",100000);

        /* Crossver operator */
        parameters = new HashMap() ;
        parameters.put("probability", 0.95) ;
        crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover_TTP", parameters);



    }
}