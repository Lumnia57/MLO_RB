import Model.MLOProblem;
import Model.Solver;

public class Main {
    public static void main(String[] args) {
        MLOProblem mloProblem = new MLOProblem(2);
        mloProblem.addConstraint("10 5",MLOProblem.LE,200);
        mloProblem.addConstraint("2 3",MLOProblem.LE,60);
        mloProblem.addConstraint("1 0",MLOProblem.LE,34);
        mloProblem.addConstraint("0 1",MLOProblem.LE,14);
        mloProblem.setObjFun("-1000 -1200");

        Solver solver = new Solver(mloProblem);
        System.out.println("--------MLO_RB--------");
        solver.solveUsingMLO_RB();
        System.out.println("\n--------LP_SOLVE--------");
        solver.solveUsingLPSolve();
    }
}