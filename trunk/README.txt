#Documentation

TTP-ea project revision 125, Group 9, 05/10/2014.  
TTP-ea project is the implementation of different evolutionary algorithms for the Traveling Thief Problem (aka. TTP). It can be compiled with Java SDK(1.6.0 65) and Ant(1.9.4).


##GENERAL USAGE NOTES

###Checkout the source code from subversion

Open terminal, and type this command to anonymously check out the latest project source code:
	
	svn checkout https://ttp-ea.googlecode.com/svn/trunk

###Folder structure

There are three folders (exercise2, exercise3, exercise4) in the root folder, each containing complete set of code to be compiled and executed independently.

Reports for each exercise are in pdf format and located in the report folder. 

###run.sh file
run.sh file in each exercise folder contains parameters about how the test will be carried out, including:

- Darg0  folder with TTP files
- Darg1  pattern to identify the TTP problems that should be solved
- Darg2  optimisation approach chosen: 1 - RLS, 2 - 1+1 EA, 3 - Local Search
- Darg3  stopping criterion: number of evaluations without improvement
- Darg4  stopping criterion: time limit in milliseconds (e.g., 60000 equals to 1 minute)


###Build and Run
To test the code for each exercise, simply open Bash, explore to the folder of corresponding exercise, and type:

	. ./run.sh [repeat]		

([repeat] is the times of repeat to be carried out on each map.))




Group Information
----------------------
1633928		Zhu Zheng  
1613340		Haijin Lin  
1638414		Tao Wang  
1640844		Yuankang Zhao  