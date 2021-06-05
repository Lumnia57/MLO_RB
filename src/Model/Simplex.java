package Model;

import java.util.Arrays;

/**
 * @author Raphaël Bagat
 * @version 0.6
 */
public class Simplex {
    private RationalNumberMatrix matrix;
    private int rows, cols;
    private boolean solutionIsUnbounded = false;
    private int nbVariables;
    private boolean isDual;
    private Integer[] notLowerBoundedVariableIndexes;

    /**
     * Constructor.
     * @param m The beginning matrix of the method.
     * @param nbVariables The number of variables in the problem.
     * @param isDual True if we are solving the dual method, false else.
     */
    public Simplex(RationalNumberMatrix m, int nbVariables, boolean isDual, Integer[] notLowerBoundedVariableIndexes){
        matrix = m.clone();
        rows = m.getRowNum();
        cols = m.getColNum();
        this.nbVariables = nbVariables;
        this.isDual = isDual;
        this.notLowerBoundedVariableIndexes = notLowerBoundedVariableIndexes;
    }

    public enum RESULT{
        NOT_OPTIMAL,
        IS_OPTIMAL,
        UNBOUNDED
    };

    /**
     * Computes the current matrix.
     * @return IS_OPTIMAL if the solution is optimal, UNBOUNDED if the solution is unbounded, else NOT_OPTIMAL.
     */
    public RESULT compute(){
        // step 1
        if(checkOptimality()){
            return RESULT.IS_OPTIMAL; // solution is optimal
        }

        // step 2
        // find the entering column
        int pivotColumn = findEnteringColumn();
        //System.out.println("Pivot Column: "+pivotColumn);

        // step 3
        // find departing value
        RationalNumber[] ratios = calculateRatios(pivotColumn);
        if(solutionIsUnbounded)
            return RESULT.UNBOUNDED;
        int pivotRow = findSmallestValue(ratios);
        //System.out.println("Pivot row: "+ pivotRow);

        // step 4
        // form the next matrix
        formNextMatrix(pivotRow, pivotColumn);

        //System.out.println("-------");System.out.println(matrix);

        // since we formed a new table so return NOT_OPTIMAL
        return RESULT.NOT_OPTIMAL;
    }

    /**
     * Forms a new matrix from precomputed pivot values.
     * @param pivotRow The pivot's row index.
     * @param pivotColumn The pivot's column index.
     */
    private void formNextMatrix(int pivotRow, int pivotColumn){
        RationalNumber pivotValue = matrix.get(pivotRow,pivotColumn);
        RationalNumber[] pivotRowVals = new RationalNumber[cols];
        RationalNumber[] pivotColumnVals = new RationalNumber[cols];
        RationalNumber[] rowNew = new RationalNumber[cols];

        // divide all entries in pivot row by entry inpivot column
        // get entry in pivot row
        System.arraycopy(matrix.getRow(pivotRow), 0, pivotRowVals, 0, cols);

        // get entry in pivot column
        for(int i = 0; i < rows; i++)
            pivotColumnVals[i] = matrix.get(i,pivotColumn);

        // divide values in pivot row by pivot value
        for(int  i = 0; i < cols; i++)
            rowNew[i] =  pivotRowVals[i].divide(pivotValue);

        // subtract from each of the other rows
        for(int i = 0; i < rows; i++){
            if(i != pivotRow){
                for(int j = 0; j < cols; j++){
                    RationalNumber c = pivotColumnVals[i];
                    matrix.set(i,j,matrix.get(i,j).subtract(c.multiply(rowNew[j])));
                }
            }
        }

        // replace the row
        matrix.setRow(pivotRow,rowNew);
    }

    /**
     * Calculates the pivot row ratios
     * @param column The pivot's column index.
     * @return The pivot row ratios in an Array.
     */
    private RationalNumber[] calculateRatios(int column){
        RationalNumber[] positiveEntries = new RationalNumber[rows-1];
        RationalNumber[] res = new RationalNumber[rows-1];

        int allNegativeCount = 0;
        for(int i = 0; i < rows-1; i++){
            if(RationalNumber.ZERO.isLessThan(matrix.get(i,column))){ //0 < matrix[i][column]
                positiveEntries[i] = matrix.get(i,column);
            }
            else{
                positiveEntries[i] = RationalNumber.ZERO;
                allNegativeCount++;
            }
        }

        if(allNegativeCount == rows-1){
            this.solutionIsUnbounded = true;
        }

        else{
            for(int i = 0;  i < rows-1; i++){
                RationalNumber val = positiveEntries[i];
                if(!val.isLessThanOrEqualTo(RationalNumber.ZERO)){ // val > 0
                    res[i] = matrix.get(i,cols-1).divide(val);
                }else{
                    res[i] = RationalNumber.ZERO;
                }
            }
        }

        return res;
    }

    /**
     * Finds the next entering column.
     * @return The next entering column index.
     */
    private int findEnteringColumn(){
        int location, pos;
        RationalNumber[] values = new RationalNumber[cols-1];

        for(pos = 0; pos < cols-1; pos++){
            values[pos] = matrix.get(rows-1,pos);
        }

        location = findLargestValue(values);

        return location;
    }


    /**
     * Finds the smallest positive value in an array.
     * @param data The array.
     * @return The index of the smallest positive value in the array.
     */
    private int findSmallestValue(RationalNumber[] data){
        RationalNumber minimum=null;
        int c, location = 0;

        for(c=0;c<data.length && minimum==null;c++){
            if(!data[c].isLessThanOrEqualTo(RationalNumber.ZERO)){ //data[c] > 0
                minimum = data[c];
                location  = c;
            }
        }

        for(c = location; c < data.length; c++){
            if(!data[c].isLessThanOrEqualTo(RationalNumber.ZERO)){ //data[c] > 0
                if(data[c].isLessThan(minimum)){ //data[c] < minimum
                    minimum = data[c];
                    location  = c;
                }
            }
        }

        return location;
    }

    /**
     * Finds the index of the largest value in an array.
     * @param data The array.
     * @return The index of the largest value in the array.
     */
    private int findLargestValue(RationalNumber[] data){
        int c, location = 0;
        RationalNumber maximum = data[0];

        for(c = 1; c < data.length; c++){
            if(maximum.isLessThan(data[c])){ //maximum < data[c]
                maximum = data[c];
                location  = c;
            }
        }

        return location;
    }

    /**
     * Checks if the matrix is optimal.
     * @return True if the matrix is optimal, else false.
     */
    public boolean checkOptimality(){
        boolean isOptimal = false;
        int vCount = 0;

        for(int i = 0; i < cols-1; i++){
            RationalNumber val = matrix.get(rows-1,i);
            if(val.isLessThanOrEqualTo(RationalNumber.ZERO)){ // val <= 0
                vCount++;
            }
        }

        if(vCount == cols-1){
            isOptimal = true;
        }

        return isOptimal;
    }

    /**
     * Get the result in a String. Use it only when done computing.
     * @return The result in a String.
     */
    public String getResult(){
        StringBuilder str = new StringBuilder();
        /*
        str.append("Matrix:\n");
        str.append(matrix.toString());
        */

        if(!isDual){
            if(!solutionIsUnbounded){
                str.append("\n\nValue of objective function: ");
                str.append(matrix.get(rows-1,cols-1));
                str.append("\n");
                RationalNumber[] res = new RationalNumber[nbVariables];
                // variables' values
                boolean flag;
                for(int i=0;i<nbVariables;i++){
                    flag = false;
                    if(matrix.doesColumnContainOneOnly(i)){
                        for(int j=0;j<rows;j++){
                            if(matrix.get(j,i).equals(RationalNumber.ONE)){
                                //str.append("Value of var["+i+"] = "+matrix.get(j,cols-1))+"\n");
                                res[i] = matrix.get(j,cols-1);
                                flag = true;
                            }
                        }
                    }
                    if(!flag){
                        //str.append("Value of var["+i+"] = 0\n");
                        res[i] = RationalNumber.ZERO;
                    }
                }
                int countVar = 0;
                for(int i=0;i<nbVariables;i++){
                    if(notLowerBoundedVariableIndexes!=null){
                        if(Arrays.asList(notLowerBoundedVariableIndexes).contains(countVar)){
                            str.append("Value of var["+countVar+"] = "+res[i].subtract(res[i+1])+"\n");
                            i++;
                        }else{
                            str.append("Value of var["+countVar+"] = "+res[i]+"\n");
                        }
                    }else{
                        str.append("Value of var["+countVar+"] = "+res[i]+"\n");
                    }
                    countVar++;
                }
            }else{ // solution is unbounded
                str.append("\n\nSolution is unbounded\n");
            }
        }else{ // isDual = true
            if(solutionIsUnbounded){
                str.append("\n\nThe problem is infeasible\n");
            }else{
                str.append("\n\nValue of objective function: ");
                str.append(matrix.get(rows-1,cols-1).multiply(RationalNumber.MINUS_ONE));
                str.append("\n");

                int count = 0;
                // variables' values
                for(int i=nbVariables;count<rows-1;i++){
                    if(notLowerBoundedVariableIndexes!=null){
                        if(Arrays.asList(notLowerBoundedVariableIndexes).contains(count)){
                            str.append("Value of var["+count+"] = "+matrix.get(rows-1,i).subtract(matrix.get(rows-1,i+1)).multiply(RationalNumber.MINUS_ONE)+"\n");
                            i++;
                        }else{
                            str.append("Value of var["+count+"] = "+matrix.get(rows-1,i).multiply(RationalNumber.MINUS_ONE)+"\n");
                        }
                    }else{
                        str.append("Value of var["+count+"] = "+matrix.get(rows-1,i).multiply(RationalNumber.MINUS_ONE)+"\n");
                    }

                    count++;
                }
            }
        }

        return str.toString();
    }
}
