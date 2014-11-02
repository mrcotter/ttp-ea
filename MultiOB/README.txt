#Documentation

##GENERAL USAGE NOTES

###Checkout the source code from subversion

Open terminal, and type this command to anonymously check out the latest project source code:
	
	svn checkout http://ttp-ea.googlecode.com/svn/trunk/MultiOB/

###Folder structure

There are three folders (exercise1, exercise2-5, document) in the root folder, exercise1 and exercise2-5 folders containing complete set of code to be compiled while document folder contains the plots of the final populations and its corresponding interpretations file.

###File add new
package jmetal.encodings.solutionType/PermutationArrayIntSolutionType
package jmetal.metaheuristics.ibea/IBEA_TTP_main
package jmetal.metaheuristics.nsgaII/NSGAII_TTP_main
package jmetal.metaheuristics.spea2/SPEA2_TTP_main
package jmetal.operators.crossover/Crossover_TTP
package jmetal.operators.mutation/Mutation_TTP
package jmetal.problems/TTP

###File modified
package jmetal.operators.crossover/CrossoverFactory
package jmetal.operators.mutation/MutationFactory

###run.sh file
run.sh file in each exercise folder contains parameters about how the test will be carried out, including:

run.sh in exercise1 folder
- arg0  problem to solve


run.sh in exercise2-5 folder
- arg0  ttp filename

###Build and Run
To test the code for each exercise, simply open Bash, explore to the folder of corresponding exercise, and type:

run.sh in exercise1 folder:	. ./run.sh [problem to solve]
run.sh in exercise2-5 folder	. ./run.sh [ttp]		


Group Information
----------------------
1633928		Zhu Zheng  
1613340		Haijin Lin  
1638414		Tao Wang  
1640844		Yuankang Zhao  