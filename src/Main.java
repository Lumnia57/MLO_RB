import Model.MLOProblem;
import Model.Solver;

public class Main {
    public static void main(String[] args) {
        MLOProblem mloProblem = new MLOProblem(2);
        /*
        mloProblem.addConstraint("55 3",MLOProblem.LE,30);
        mloProblem.addConstraint("2 3",MLOProblem.LE,24);
        mloProblem.addConstraint("1 3",MLOProblem.LE,18);
        */
        mloProblem.addConstraint("55/1 3/1",MLOProblem.LE,30);
        mloProblem.addConstraint("2/3 645/3",MLOProblem.LE,24);
        mloProblem.addConstraint("571 35",MLOProblem.LE,18);
        mloProblem.addConstraint("-45/8 -8",MLOProblem.LE,-54);
        mloProblem.addConstraint("-8/54 -9/564",MLOProblem.LE,8);
        mloProblem.setObjFun("-8 -6");

        Solver solver = new Solver(mloProblem);
        //solver.solveUsingLPSolve();

        solver.solveUsingMLO_RB();
    }
}