package Model;

/**
 * @author Raphaël Bagat
 * @version 1.0
 */
public class MLO_RB {
    /**
     * Constructor.
     */
    public MLO_RB() { }

    /**
     * Solves the MLO problem using the simplex method and prints the solution in the standard ouput.
     * The MLO problem has to be feasible.
     */
    public void solve(RationalNumberMatrix matrixStart, String objFun, int nbVariables){
        /*
        System.out.println("Initial matrix:");
        System.out.println(matrixStart);
        System.out.println("\nmin " + objFun+"\n");
        */
        boolean quit = false;

        Simplex simplex = new Simplex(matrixStart,nbVariables);

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
     * @param mloProblem The MLO problem.
     */
    public void checkFeasibility(MLOProblem mloProblem){
        PhaseOne p = new PhaseOne(mloProblem.getB(),mloProblem.getTypes(),mloProblem.getNbRows(),mloProblem.getNbVar(),ProblemToMatrixTransformation.problemToNormalizedProblemMatrixForPhaseOne(mloProblem));
        p.compute();
        System.out.println(p.getResult());
    }
}
