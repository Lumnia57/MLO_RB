import Model.MLOProblem;
import Model.Solver;

public class Main {
    public static void main(String[] args) {
        MLOProblem mloProblem = new MLOProblem(2);
        mloProblem.addConstraint("5 3",MLOProblem.LE,30);
        mloProblem.addConstraint("2 3",MLOProblem.LE,24);
        mloProblem.addConstraint("1 3",MLOProblem.LE,18);
        mloProblem.setObjFun("-8 -6");

        Solver solver = new Solver(mloProblem);
        solver.solveUsingLPSolve();
    }
}
