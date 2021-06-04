package Model;

/**
 * @author Raphaël Bagat
 * @version 1.0
 */
public class MLO_RB {
    private MLOProblem mloProblem;

    /**
     * Constructor.
     */
    public MLO_RB(MLOProblem mloProblem) {
        this.mloProblem = mloProblem;
    }

    /**
     * Solves the MLO problem using the simplex method and prints the solution in the standard ouput.
     * The MLO problem has to be feasible.
     */
    public void solve(){
        RationalNumberMatrix matrixStart = ProblemToMatrixTransformation.problemToNormalizedProblemMatrix(mloProblem);

        boolean quit = false;

        // check if all the coefficients in the objective function are negative
        // if so, we solve the dual problem.
        boolean flag = true;
        for(int c=0;c<matrixStart.getColNum()-1 && flag;c++){
            flag = flag && matrixStart.get(matrixStart.getRowNum()-1,c).isLessThanOrEqualTo(RationalNumber.ZERO);
        }

        Simplex simplex;
        if(flag) { // all the coefficients are negative
            matrixStart = ProblemToMatrixTransformation.problemToDualProblemMatrix(mloProblem);
        }

        int nbVariables = matrixStart.getColNum()-matrixStart.getRowNum();
        simplex = new Simplex(matrixStart,nbVariables,flag, mloProblem.getNotLowerBoundedVariableIndexes());

        while(!quit){
            Simplex.RESULT res = simplex.compute();

            if(res == Simplex.RESULT.IS_OPTIMAL || res == Simplex.RESULT.UNBOUNDED){
                quit = true;
                System.out.println(simplex.getResult());
            }
        }
    }

    /**
     * Checks if the MLO problem given as parameter is feasible.
     */
    public void checkFeasibility(){
        MLOProblem mloProblem = this.mloProblem.clone();
        PhaseOne p = new PhaseOne(mloProblem.getB(),mloProblem.getTypes(),mloProblem.getNbRows(),mloProblem.getNbVar(),ProblemToMatrixTransformation.problemToNormalizedProblemMatrixForPhaseOne(mloProblem));
        p.compute();
        System.out.println(p.getResult());
    }
}
