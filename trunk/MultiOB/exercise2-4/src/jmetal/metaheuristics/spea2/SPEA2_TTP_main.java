package jmetal.metaheuristics.spea2;

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
 * Class for configuring and running the SPEA2 algorithm
 * It is aimed at solving the TTP problem.
 */
public class SPEA2_TTP_main {
    public static Logger      logger_ ;      // Logger object
    public static FileHandler fileHandler_ ; // FileHandler object

    /**
     * @param args Command line arguments. The first (optional) argument specifies
     *             the problem to solve.
     * @throws JMException
     * @throws IOException
     * @throws SecurityException
     * Usage: three options
     *      - jmetal.metaheuristics.mocell.MOCell_main
     *      - jmetal.metaheuristics.mocell.MOCell_main ttp_fileName
     */
    public static void main(String [] args) throws JMException, IOException, ClassNotFoundException {
        Problem   problem = null ;         // The problem to solve
        Algorithm algorithm ;         // The algorithm to use
        Operator  crossover ;         // Crossover operator
        Operator  mutation  ;         // Mutation operator
        Operator  selection ;         // Selection operator

        QualityIndicator indicators ; // Object to get quality indicators

        HashMap  parameters ; // Operator parameters

        // Logger object and file to store log messages
        logger_      = Configuration.logger_ ;
        fileHandler_ = new FileHandler("SPEA2_TTP.log");
        logger_.addHandler(fileHandler_) ;

        indicators = null ;
        if (args.length == 0) {
            problem = new TTP("PermutationArrayInt", "a280_n279_bounded-strongly-corr_01.ttp");
        }
        else if (args.length == 1) {
            problem = new TTP("PermutationArrayInt", args[0]);
        } else {
            System.err.println("Usage: - jmetal.metaheuristics.spea2.SPEA2_TTP_main ttp_fileName");
            System.exit(1);
        }

        algorithm = new SPEA2(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize",100);
        algorithm.setInputParameter("archiveSize",100);
        algorithm.setInputParameter("maxEvaluations",1000000);

         /* Crossver operator */
        parameters = new HashMap() ;
        parameters.put("probability", 0.5) ;
        crossover = CrossoverFactory.getCrossoverOperator("PMXCrossover_TTP", parameters);

        /* Mutation operator */
        parameters = new HashMap() ;
        parameters.put("probability", 0.2) ;
        mutation = MutationFactory.getMutationOperator("Mutation_TTP", parameters);

        /* Selection Operator */
        parameters = null;
        selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

        // Add the operators to the algorithm
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);

        // Execute the algorithm
        long initTime = System.currentTimeMillis();
        SolutionSet population = algorithm.execute();
        long estimatedTime = System.currentTimeMillis() - initTime;

        // Result messages
        logger_.info("Total execution time: "+estimatedTime + "ms");
        logger_.info("Objectives values have been writen to file FUN");
        population.printObjectivesToFile("FUN");
        logger_.info("Variables values have been writen to file VAR");
        population.printVariablesToFile("VAR");

        if (indicators != null) {
            logger_.info("Quality indicators") ;
            logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
            logger_.info("GD         : " + indicators.getGD(population)) ;
            logger_.info("IGD        : " + indicators.getIGD(population)) ;
            logger_.info("Spread     : " + indicators.getSpread(population)) ;
            logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;
        } // if
    }

}
