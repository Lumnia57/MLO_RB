package Model;

public class Simplex {
    private RationalNumberMatrix matrix;
    private int rows, cols;
    private boolean solutionIsUnbounded = false;

    /**
     * Constructor.
     * @param m The beginning matrix of the method.
     */
    public Simplex(RationalNumberMatrix m){
        matrix = m.clone();
        rows = m.getRowNum();
        cols = m.getColNum();
    }

    public static enum RESULT{
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
        RationalNumber[] positiveEntries = new RationalNumber[rows];
        RationalNumber[] res = new RationalNumber[rows];

        int allNegativeCount = 0;
        for(int i = 0; i < rows; i++){
            if(!matrix.get(i,column).isLessThanOrEqualTo(RationalNumber.ZERO)){ //matrix[i][column] > 0
                positiveEntries[i] = matrix.get(i,column);
            }
            else{
                positiveEntries[i] = RationalNumber.ZERO;
                allNegativeCount++;
            }
            //System.out.println(positiveEntries[i]);
        }

        if(allNegativeCount == rows)
            this.solutionIsUnbounded = true;
        else{
            for(int i = 0;  i < rows; i++){
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
        RationalNumber[] values = new RationalNumber[cols];
        int location = 0;

        int pos, count = 0;
        for(pos = 0; pos < cols-1; pos++){
            if(matrix.get(rows-1,pos).add(RationalNumber.ONE).isLessThanOrEqualTo(RationalNumber.ZERO)){ //matrx[rows-1][pos] < 0
                //System.out.println("negative value found");
                count++;
            }
        }

        if(count > 1){
            for(int i = 0; i < cols-1; i++)
                values[i] = matrix.get(rows-1,i).absoluteValue();
            location = findLargestValue(values);
        } else location = count; //count -1 ?????

        return location;
    }


    /**
     * Finds the smallest positive value in an array.
     * @param data The array.
     * @return The index of the smallest positive value in the array.
     */
    private int findSmallestValue(RationalNumber[] data){
        RationalNumber minimum ;
        int c, location = 0;
        minimum = data[0];

        for(c = 1; c < data.length; c++){
            //System.out.println(data[c]);
            if(!data[c].isLessThanOrEqualTo(RationalNumber.ZERO)){ //data[c] > 0
                if(data[c].add(RationalNumber.ONE).isLessThanOrEqualTo(minimum)){ //data[c] < minimum
                    minimum = data[c];
                    location  = c;
                }
            }
        }

        return location;
    }

    /**
     * Finds the index of the largest value in an array
     * @param data The array.
     * @return The index of the largest value in the array.
     */
    private int findLargestValue(RationalNumber[] data){
        RationalNumber maximum;
        int c, location = 0;
        maximum = data[0];

        for(c = 1; c < data.length; c++){
            if(maximum.add(RationalNumber.ONE).isLessThanOrEqualTo(data[c])){ //maximum < data[c]
                maximum = data[c];
                location  = c;
            }
        }

        return location;
    }

    /**
     * Checks if the matrix is optimal
     * @return True if the matrix is optimal, else false.
     */
    public boolean checkOptimality(){
        boolean isOptimal = false;
        int vCount = 0;

        for(int i = 0; i < cols; i++){
            RationalNumber val = matrix.get(rows-1,i);
            if(val.isLessThanOrEqualTo(RationalNumber.ZERO.subtract(RationalNumber.ONE))){ // val < 0
            //if(RationalNumber.ZERO.isLessThanOrEqualTo(val)){ // 0 <= val
                vCount++;
            }
        }

        if(vCount == (cols-1)/2){
            isOptimal = true;
        }

        return isOptimal;
    }

    public RationalNumberMatrix getResult(){
        return matrix;
    }
}