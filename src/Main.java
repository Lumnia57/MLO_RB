import Model.MLOProblem;
import Model.RationalNumber;
import Model.RationalNumberMatrix;
import Model.Solver;

public class Main {
    public static void main(String[] args) {
        MLOProblem mloProblem = new MLOProblem(3);
        /*
        mloProblem.addConstraint("55 3",MLOProblem.LE,30);
        mloProblem.addConstraint("2 3",MLOProblem.LE,24);
        mloProblem.addConstraint("1 3",MLOProblem.LE,18);
        */
        mloProblem.addConstraint("32/4 8/3 6",MLOProblem.LE,30);
        mloProblem.addConstraint("43 67/5 8",MLOProblem.LE,24);
        mloProblem.addConstraint("571 35 24",MLOProblem.LE,18);
        mloProblem.addConstraint("79/6 1 4",MLOProblem.LE,-54);
        mloProblem.setObjFun("-8 -6 6");

        Solver solver = new Solver(mloProblem);

        RationalNumberMatrix m = solver.problemToMatrix();

        System.out.println(m);

        m.multiplyColumnByScalar(0,new RationalNumber(2,4));
        System.out.println("--------");
        System.out.println(m);
    }
}