package Model;

public class MLO_RB {
    private RationalNumberMatrix matrixStart;
    private String objFun;

    /**
     * Constructor.
     * @param matrix The MLO problem in the form of a matrix.
     * @param objFun The objective function in a String.
     */
    public MLO_RB(RationalNumberMatrix matrix, String objFun) {
        this.matrixStart = matrix;
        this.objFun = objFun;
    }

    /**
     * Solves the MLO problem using the simplex method and prints the solution in the standard ouput.
     */
    public void solve(){
        System.out.println("Initial matrix:");
        System.out.println(matrixStart);
        System.out.println("\nmin " + objFun+"\n");
        boolean quit = false;

        Simplex simplex = new Simplex(matrixStart);

        while(!quit){
            Simplex.RESULT res = simplex.compute();

            if(res == Simplex.RESULT.IS_OPTIMAL){
                quit = true;
                System.out.println("---Result:---");
                System.out.println(simplex.getResult());
            }
            else if(res == Simplex.RESULT.UNBOUNDED){
                System.out.println("---Solution is unbounded---");
                quit = true;
            }
        }
    }
}
