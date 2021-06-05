package Model;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raphaël Bagat
 * @version 0.3
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
            int numAddedVariables = 0;
            if(mloProblem.getNotLowerBoundedVariableIndexes()!=null){
                numAddedVariables = mloProblem.getNotLowerBoundedVariableIndexes().length;
            }
            LpSolve solver = LpSolve.makeLp(0, mloProblem.getNbVar()+numAddedVariables);
            solver.setVerbose(3);

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

                // get the right hand side value
                RationalNumber b = new RationalNumber(mloProblem.getB().get(i));

                solver.strAddConstraint(getRowWithAddedVariables(i,false),consType, b.toDouble());
            }

            // set objective function
            solver.strSetObjFn(getRowWithAddedVariables(0,true));

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

    private String getRowWithAddedVariables(int index, boolean isObjectiveFunction){
        StringBuilder str = new StringBuilder();
        Matcher m;
        Pattern p;

        /* row string to string list */
        LinkedList<String> sl = new LinkedList<>();
        LinkedList<RationalNumber> rl = new LinkedList<>();
        p = Pattern.compile("[-]?[0-9]+([/][-]?[0-9]+)?");

        if(isObjectiveFunction){
            m = p.matcher(mloProblem.getObjFun());
        }else{
            m = p.matcher(mloProblem.getValues().get(index));
        }

        while (m.find()){
            sl.addLast(m.group(0));
        }
        /* string list to rational number list */
        int countVar = 0;
        boolean flag;
        if(mloProblem.getNotLowerBoundedVariableIndexes()!=null){
            for(String s2 : sl){
                flag = Arrays.asList(mloProblem.getNotLowerBoundedVariableIndexes()).contains(countVar);
                rl.addLast(new RationalNumber(s2));
                if(flag){
                    rl.addLast(new RationalNumber(s2).multiply(RationalNumber.MINUS_ONE));
                }
                countVar++;
            }
        }else{
            for(String s2 : sl){
                rl.addLast(new RationalNumber(s2));
            }
        }
        for(RationalNumber n : rl){
            str.append(n.toDouble()+" ");
        }
        str.deleteCharAt(str.length()-1);

        return str.toString();
    }

    /**
     * Solves the MLO problem using MLO_RB and prints the results in the standard output.
     */
    public void solveUsingMLO_RB(){
        MLO_RB mlo_rb = new MLO_RB(mloProblem);
        mlo_rb.solve();
    }

    /**
     * Check if the MLO problem is feasible using MLO_RB and prints the result in the standard output.
     */
    public void checkFeasibilityUsingMLO_RB(){
        MLO_RB mlo_rb = new MLO_RB(mloProblem);
        mlo_rb.checkFeasibility();
    }
}
