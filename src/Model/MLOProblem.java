package Model;

import java.util.LinkedList;

/**
 * @author Raphaël Bagat
 * @version 1.1
 */
public class MLOProblem {
    private LinkedList<String> values;
    private LinkedList<String> B;
    private LinkedList<Integer> types;
    private String objFun;
    private int nbVar;
    public static Integer LE = 0;
    public static Integer EQ = 1;
    public static Integer GE = 2;

    /**
     * Constructor.
     * @param nbVar The number of variables in the problem.
     */
    public MLOProblem(int nbVar){
        assert(nbVar>=0):"";
        values = new LinkedList<>();
        B = new LinkedList<>();
        types = new LinkedList<>();
        this.nbVar = nbVar;
    }

    /**
     * Adds a constraint in the problem.
     * @param values A String that contains the value of the row. Format: v1 v2 v3 ...
     * @param type The type of the constraint. (Less than or equal LE, Equal EQ, Greater than or equal GE)
     * @param b The value of the right hand side.
     */
    public void addConstraint(String values, int type, double b){
        B.addLast(String.valueOf(b));
        this.values.addLast(values);
        types.addLast(type);
    }

    /**
     * Sets the objective function of the problem.
     * @param objFun A String that contains the value of the row.
     */
    public void setObjFun(String objFun){
        this.objFun = objFun;
    }

    /**
     * Gets the values of the constraints in a LinkedList. Format: v1 v2 v3 ...
     * @return The values of the constraints in a LinkedList. Format: v1 v2 v3 ...
     */
    public LinkedList<String> getValues() {
        return values;
    }

    /**
     * Gets the values of the right hand side vales.
     * @return The values of the right hand side values.
     */
    public LinkedList<String> getB() {
        return B;
    }

    /**
     * Gets the types of the constraints in a LinkedList.
     * @return The types of the constraints in a LinkedList.
     */
    public LinkedList<Integer> getTypes() {
        return types;
    }

    /**
     * Gets the objective function.
     * @return The objective function.
     */
    public String getObjFun() {
        return objFun;
    }

    /**
     * Gets the number of variables.
     * @return The number of variables.
     */
    public int getNbVar() {
        return nbVar;
    }

    /**
     * Gets the number of columns.
     * @return The number of columns.
     */
    public int getNbRows(){
        return B.size();
    }

    @Override
    public MLOProblem clone(){
        MLOProblem newPb = new MLOProblem(nbVar);
        newPb.values = (LinkedList<String>) values.clone();
        newPb.B = (LinkedList<String>) B.clone();
        newPb.types = (LinkedList<Integer>) types.clone();
        newPb.objFun = objFun;

        return newPb;
    }
}
