import Model.*;

public class Main {
    public static void main(String[] args) {
        //testWorkingCase4Variables3Constraints();
        //testWorkingCase3Variables4Constraints();
        //testWorkingCaseWithUnboundedSolution();
        //testInfeasibleCase();
        //testExponentialCase();

        test();
    }

    public static void testWorkingCase4Variables3Constraints(){
        MLOProblem mloProblem = new MLOProblem(4);
        mloProblem.addConstraint("10 5 -5 12",MLOProblem.LE,"200");
        mloProblem.addConstraint("8 9 5 1",MLOProblem.LE,"664");
        mloProblem.addConstraint("9 6 4 7",MLOProblem.LE,"668");
        mloProblem.addConstraint("35 12 12 54",MLOProblem.LE,"346");
        mloProblem.setObjFun("-564 -986 -32 -8");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
        /*
        --------CHECKING FEASIBILITY--------
        The problem is feasible.
        --------SOLVING MLO_RB--------
        Value of objective function: -85289/3
        Value of var[0] = 0
        Value of var[1] = 173/6
        Value of var[2] = 0
        Value of var[3] = 0
        --------LP_SOLVE--------
        Value of objective function: -28429.666666666664
        Value of var[0] = 0.0
        Value of var[1] = 28.83333333333333
        Value of var[2] = 0.0
        Value of var[3] = 0.0
        */
    }

    public static void testWorkingCase3Variables4Constraints(){
        MLOProblem mloProblem = new MLOProblem(3);
        mloProblem.addConstraint("8 6 -2",MLOProblem.LE,"65");
        mloProblem.addConstraint("1 -2 4",MLOProblem.LE,"24");
        mloProblem.addConstraint("0 2 3",MLOProblem.LE,"59");
        mloProblem.addConstraint("-2 6 0",MLOProblem.LE,"23");
        mloProblem.setObjFun("-95 -84 -6");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
        /*
        --------CHECKING FEASIBILITY--------
        The problem is feasible.
        --------SOLVING MLO_RB--------
        Value of objective function: -65047/61
        Value of var[0] = 347/61
        Value of var[1] = 699/122
        Value of var[2] = 454/61
        --------LP_SOLVE--------
        Value of objective function: -1066.344262295082
        Value of var[0] = 5.688524590163935
        Value of var[1] = 5.729508196721311
        Value of var[2] = 7.442622950819672
        */
    }

    public static void testWorkingCaseWithUnboundedSolution(){
        MLOProblem mloProblem = new MLOProblem(2);
        mloProblem.addConstraint("1 0",MLOProblem.LE,"5");
        mloProblem.addConstraint("0 -1",MLOProblem.LE,"3");
        mloProblem.setObjFun("1 -2");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
        /*
        --------CHECKING FEASIBILITY--------
        The problem is feasible.
        --------SOLVING MLO_RB--------
        Solution is unbounded
        --------LP_SOLVE--------
        Value of objective function: 1.0E30
        Value of var[0] = 0.0
        Value of var[1] = 0.0
        */
    }

    public static void testInfeasibleCase(){
        MLOProblem mloProblem = new MLOProblem(3);
        mloProblem.addConstraint("1 1 6",MLOProblem.LE,"5");
        mloProblem.addConstraint("-1 -1 8",MLOProblem.LE,"-6");
        mloProblem.setObjFun("-1 2 3");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
        /*
        --------CHECKING FEASIBILITY--------
        The problem is infeasible.
        --------LP_SOLVE--------
        Value of objective function: 1.0E30
        Value of var[0] = 0.0
        Value of var[1] = 0.0
        Value of var[2] = 0.0
        */
    }

    public static void testExponentialCase(){
        MLOProblem mloProblem = new MLOProblem(10);
        mloProblem.addConstraint("1 0 0 0 0 0 0 0 0 0",MLOProblem.LE,"5");
        mloProblem.addConstraint("4 1 0 0 0 0 0 0 0 0",MLOProblem.LE,"25");
        mloProblem.addConstraint("8 4 1 0 0 0 0 0 0 0",MLOProblem.LE,"125");
        mloProblem.addConstraint("16 8 4 1 0 0 0 0 0 0",MLOProblem.LE,"625");
        mloProblem.addConstraint("32 16 8 4 1 0 0 0 0 0",MLOProblem.LE,"3125");
        mloProblem.addConstraint("64 32 16 8 4 1 0 0 0 0",MLOProblem.LE,"15625");
        mloProblem.addConstraint("128 64 32 16 8 4 1 0 0 0",MLOProblem.LE,"78125");
        mloProblem.addConstraint("256 128 64 32 16 8 4 1 0 0",MLOProblem.LE,"390625");
        mloProblem.addConstraint("512 256 128 64 32 16 8 4 1 0",MLOProblem.LE,"1953125");
        mloProblem.addConstraint("1024 512 256 128 64 32 16 8 4 1",MLOProblem.LE,"9765625");
        mloProblem.setObjFun("-512 -256 -128 -64 -32 -16 -8 -4 -2 -1");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
        /*
        --------CHECKING FEASIBILITY--------
        The problem is feasible.
        --------SOLVING MLO_RB--------
        Value of objective function: -9765625
        Value of var[0] = 0
        Value of var[1] = 0
        Value of var[2] = 0
        Value of var[3] = 0
        Value of var[4] = 0
        Value of var[5] = 0
        Value of var[6] = 0
        Value of var[7] = 0
        Value of var[8] = 0
        Value of var[9] = 9765625
        --------LP_SOLVE--------
        Value of objective function: -9765625.0
        Value of var[0] = 0.0
        Value of var[1] = 0.0
        Value of var[2] = 0.0
        Value of var[3] = 0.0
        Value of var[4] = 0.0
        Value of var[5] = 0.0
        Value of var[6] = 0.0
        Value of var[7] = 0.0
        Value of var[8] = 0.0
        Value of var[9] = 9765625.0
        */
    }

    public static void test(){

        MLOProblem mloProblem = new MLOProblem(3);
        mloProblem.setNotLowerBoundedVariableIndexes(0,1,2);
        mloProblem.addConstraint("-9 1 8",MLOProblem.GE,"4");
        mloProblem.addConstraint("3 -2 4",MLOProblem.LE,"35");
        mloProblem.addConstraint("1 0 0",MLOProblem.LE,"10");
        mloProblem.addConstraint("1 0 0",MLOProblem.GE,"-10");
        mloProblem.addConstraint("0 1 0",MLOProblem.LE,"10");
        mloProblem.addConstraint("0 1 0",MLOProblem.GE,"-10");
        mloProblem.addConstraint("0 0 1",MLOProblem.LE,"10");
        mloProblem.addConstraint("0 0 1",MLOProblem.GE,"-10");
        mloProblem.setObjFun("1 -3 2");

        Solver solver = new Solver(mloProblem);
        //System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        //System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        //System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();


        System.out.println(ProblemToMatrixTransformation.problemToNormalizedProblemMatrix(mloProblem));
        System.out.println(ProblemToMatrixTransformation.problemToNormalizedProblemMatrixForPhaseOne(mloProblem));

    /*
        mloProblem = new MLOProblem(6);
        mloProblem.addConstraint("-9 9 1 -1 8 -8",MLOProblem.GE,"4");
        mloProblem.addConstraint("3 -3 -2 2 4 -4",MLOProblem.LE,"35");
        mloProblem.addConstraint("1 -1 0 0 0 0",MLOProblem.LE,"10");
        mloProblem.addConstraint("1 -1 0 0 0 0",MLOProblem.GE,"-10");
        mloProblem.addConstraint("0 0 1 -1 0 0",MLOProblem.LE,"10");
        mloProblem.addConstraint("0 0 1 -1 0 0",MLOProblem.GE,"-10");
        mloProblem.addConstraint("0 0 0 0 1 -1",MLOProblem.LE,"10");
        mloProblem.addConstraint("0 0 0 0 1 -1",MLOProblem.GE,"-10");
        mloProblem.setObjFun("1 -1 -3 3 2 -2");
        solver = new Solver(mloProblem);

        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();


     */




        /*
        probleme avec lp-solve qui ajoute pas les variables
        probleme avec checking feasibility
        */
    }
}