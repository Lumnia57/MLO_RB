package Model;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

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
            LpSolve solver = LpSolve.makeLp(0, mloProblem.getNbVar());
            //solver.setVerbose(3);

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
