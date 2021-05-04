package Model;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raphaël Bagat
 * @version 0.2
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
        MLO_RB mlo_rb = new MLO_RB(problemToNormalizedProblemMatrix(),mloProblem.getObjFun());
        mlo_rb.solve();
    }

    /**
     * Converts the problem to a RationalNumberMatrix.
     * @return The problem converted to a RationalNumberMatrix.
     */
    public RationalNumberMatrix problemToNormalizedProblemMatrix(){
        MLOProblem mloProblem = this.mloProblem.clone();
        int numAddedVariables = mloProblem.getNbRows();
        int rowNum = mloProblem.getNbRows() + 1;
        int colNum = mloProblem.getNbVar() + 1 + numAddedVariables;
        RationalNumberMatrix matrix = new RationalNumberMatrix(rowNum,colNum);

        LinkedList<String> sl = new LinkedList<>();
        Pattern p;
        Matcher m;

        LinkedList<RationalNumber> rl = new LinkedList<>();
        int num=0;
        int denom;
        int count;
        int countAddedVar;

        int index = 0;
        RationalNumber[] row;

        LinkedList<String> toBeConverted = mloProblem.getValues();
        toBeConverted.addLast(mloProblem.getObjFun());

        /* row string to matrix */
        for(String s1 : toBeConverted){
            rl.clear();
            sl.clear();
            countAddedVar=0;
            while(countAddedVar<numAddedVariables) {
                if (countAddedVar == index)
                    s1 += " 1";
                else
                    s1 += " 0";
                countAddedVar++;
            }

            /* row string to string list */
            p = Pattern.compile("[-]?[0-9]+([/][-]?[0-9]+)?");
            m = p.matcher(s1);
            while (m.find()){
                sl.addLast(m.group(0));
            }

            /* string list to rational number list */
            p = Pattern.compile("[-]?[0-9]+");
            count = 0;
            for(String s2 : sl){
                m = p.matcher(s2);
                while (m.find()){
                    if(s2.contains("/")){
                        if(count%2==0){
                            num = Integer.parseInt(m.group(0));
                        }else{
                            denom = Integer.parseInt(m.group(0));
                            rl.addLast(new RationalNumber(num,denom));
                        }
                        count++;
                    }else{
                        num = Integer.parseInt(m.group(0));
                        denom = 1;
                        rl.addLast(new RationalNumber(num,denom));
                    }
                }
            }

            row = rl.toArray(new RationalNumber[rl.size()+1]);
            if(index<rowNum-1){
                row[colNum-1] = new RationalNumber((int)Double.parseDouble(mloProblem.getB().get(index)),1); /* works because B is filled with int */
            }else{
                row[colNum-1] = new RationalNumber(0,1);
            }

            matrix.addRow(row,index);
            index++;
        }

        return matrix;
    }
}
