package Model;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raphaël Bagat
 * @version 1.0
 */
public class Solver {
    private MLOProblem mloProblem;

    /**
     * Constructor. This solver minimizes the objective funtion.
     * @param mloProblem The MLO problem to solve.
     */
    public Solver(MLOProblem mloProblem){
        this.mloProblem = mloProblem;
    }

    /**
     * Solves the MLO problem using LP_Solve and prints the results in the standard output.
     */
    public void solveUsingLPSolve(){
        try {
            // Create a problem with 4 variables and 0 constraints
            LpSolve solver = LpSolve.makeLp(0, mloProblem.getNbVar());

            int nbCons = mloProblem.getValues().size();
            int consType;
            // add constraints
            for(int i=0;i<nbCons;i++){
                consType = -1;
                switch(mloProblem.getTypes().get(i)){
                    case 0:
                        consType = LpSolve.LE;
                        break;
                    case 1:
                        consType = LpSolve.EQ;
                        break;
                    case 2:
                        consType = LpSolve.GE;
                        break;
                }
                solver.strAddConstraint(mloProblem.getValues().get(i),consType, Double.parseDouble(mloProblem.getB().get(i)));
            }

            // set objective function
            solver.strSetObjFn(mloProblem.getObjFun());

            // solve the problem
            solver.solve();

            // print solution
            System.out.println("Value of objective function: " + solver.getObjective());
            double[] var = solver.getPtrVariables();
            for (int i = 0; i < var.length; i++) {
                System.out.println("Value of var[" + i + "] = " + var[i]);
            }

            // delete the problem and free memory
            solver.deleteLp();
        }
        catch (LpSolveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Solves the MLO problem using MLO_RB and prints the results in the standard output.
     */
    public void solveUsingMLO_RB(){
        System.out.println(problemToMatrix());
    }

    /**
     * Converts the problem to a RationalNumberMatrix.
     * @return The problem converted to a RationalNumberMatrix.
     */
    private RationalNumberMatrix problemToMatrix(){
        int rowNum = mloProblem.getB().size();
        int colNum = mloProblem.getNbVar() + 1;
        RationalNumberMatrix matrix = new RationalNumberMatrix(rowNum,colNum);

        LinkedList<String> sl = new LinkedList<>();
        Pattern p;
        Matcher m;

        LinkedList<RationalNumber> rl = new LinkedList<>();
        int num=0;
        int denom;
        int count = 0;

        int index = 0;
        RationalNumber[] row;

        /* row string to matrix */
        for(String s1 : mloProblem.getValues()){
            rl.clear();
            sl.clear();
            /* row string to string list */
            p = Pattern.compile("[0-9]+([/][0-9])?");
            m = p.matcher(s1);
            while (m.find()){
                sl.addLast(m.group(0));
            }

            /* string list to rational number list */
            p = Pattern.compile("[0-9]+");
            for(String s2 : sl){
                m = p.matcher(s2);
                while (m.find()){
                    if(count%2==0){
                        num = Integer.parseInt(m.group(0));
                    }else{
                        if(s2.contains("/")){
                            denom = Integer.parseInt(m.group(0));
                        }else{
                            denom = 1;
                        }
                        rl.addLast(new RationalNumber(num,denom));
                    }
                    count++;
                }
            }
            /* the previous loop doesn't include the last element of sl if it doesn't contain a "/"
             * so we have to include it if it's the case */
            if(!sl.getLast().contains("/")){
                num = Integer.parseInt(sl.getLast());
                rl.addLast(new RationalNumber(num,1));
            }

            row = rl.toArray(new RationalNumber[rl.size()+1]);
            row[colNum-1] = new RationalNumber((int)Double.parseDouble(mloProblem.getB().get(index)),1); /* works because B is filled with int */

            matrix.addRow(row,index);
            index++;
        }

        return matrix;
    }
}
