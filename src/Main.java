import Model.*;

// REGLER PROBLEME AVEC >=
public class Main {
    public static void main(String[] args) {
        //testWorkingCase4Variables3Constraints();
        //testWorkingCase3Variables4Constraints();
        //testWorkingCaseWithUnboundedSolution();
        //testUnfeasibleCase();
        testExponentialCase();
    }

    public static void testWorkingCase4Variables3Constraints(){
        MLOProblem mloProblem = new MLOProblem(4);
        mloProblem.addConstraint("10 5 -5 12",MLOProblem.LE,200);
        mloProblem.addConstraint("8 9 5 1",MLOProblem.LE,664);
        mloProblem.addConstraint("9 6 4 7",MLOProblem.LE,668);
        mloProblem.addConstraint("35 12 12 54",MLOProblem.LE,346);
        mloProblem.setObjFun("-564 -986 -32 -8");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
    }

    public static void testWorkingCase3Variables4Constraints(){
        MLOProblem mloProblem = new MLOProblem(3);
        mloProblem.addConstraint("8 6 -2",MLOProblem.LE,65);
        mloProblem.addConstraint("1 -2 4",MLOProblem.LE,26);
        mloProblem.addConstraint("0 2 3",MLOProblem.LE,59);
        mloProblem.addConstraint("-2 6 0",MLOProblem.LE,23);
        mloProblem.setObjFun("-95 -84 -6");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
    }

    public static void testWorkingCaseWithUnboundedSolution(){
        MLOProblem mloProblem = new MLOProblem(2);
        mloProblem.addConstraint("1 0",MLOProblem.LE,5);
        mloProblem.addConstraint("0 -1",MLOProblem.LE,3);
        mloProblem.setObjFun("1 -2");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
    }

    public static void testUnfeasibleCase(){
        MLOProblem mloProblem = new MLOProblem(3);
        mloProblem.addConstraint("1 1 6",MLOProblem.LE,5);
        mloProblem.addConstraint("-1 -1 8",MLOProblem.LE,-6);
        mloProblem.setObjFun("-1 2 3");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
    }

    public static void testExponentialCase(){
        MLOProblem mloProblem = new MLOProblem(10);
        mloProblem.addConstraint("1 0 0 0 0 0 0 0 0 0",MLOProblem.LE,5);
        mloProblem.addConstraint("4 1 0 0 0 0 0 0 0 0",MLOProblem.LE,25);
        mloProblem.addConstraint("8 4 1 0 0 0 0 0 0 0",MLOProblem.LE,125);
        mloProblem.addConstraint("16 8 4 1 0 0 0 0 0 0",MLOProblem.LE,625);
        mloProblem.addConstraint("32 16 8 4 1 0 0 0 0 0",MLOProblem.LE,3125);
        mloProblem.addConstraint("64 32 16 8 4 1 0 0 0 0",MLOProblem.LE,15625);
        mloProblem.addConstraint("128 64 32 16 8 4 1 0 0 0",MLOProblem.LE,78125);
        mloProblem.addConstraint("256 128 64 32 16 8 4 1 0 0",MLOProblem.LE,390625);
        mloProblem.addConstraint("512 256 128 64 32 16 8 4 1 0",MLOProblem.LE,1953125);
        mloProblem.addConstraint("1024 512 256 128 64 32 16 8 4 1",MLOProblem.LE,9765625);
        mloProblem.setObjFun("-512 -256 -128 -64 -32 -16 -8 -4 -2 -1");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------CHECKING FEASIBILITY--------");solver.checkFeasibilityUsingMLO_RB();
        System.out.println("\n--------SOLVING MLO_RB--------");solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");solver.solveUsingLPSolve();
    }
}