package Model;

import java.util.LinkedList;

public class PhaseOne {
    private RationalNumberMatrix matrix;
    private RationalNumberMatrix matrixStart;
    private int rows, cols;
    private boolean solutionIsUnbounded = false;
    private int nbVariables;
    private LinkedList<Integer> rowsWithArtificialVar = new LinkedList<>();
    private LinkedList<Integer> colsWithArtificialVar = new LinkedList<>();
    private LinkedList<Integer> colsInBase = new LinkedList<>();
    private LinkedList<String> B;
    private LinkedList<Integer> types;

    public PhaseOne(MLOProblem mloProblem, RationalNumberMatrix mStart) {
        init(mloProblem, mStart);
        nbVariables = mloProblem.getNbVar();
        matrixStart = matrix.clone();
    }

    private void init(MLOProblem mloProblem, RationalNumberMatrix mStartP){
        RationalNumberMatrix mStart = mStartP.clone();

        // we want to change all the constraints so that all Bi are positive
        B = mloProblem.getB();
        types = mloProblem.getTypes();
        for(int i=0;i<B.size();i++){
            double val = Double.parseDouble(B.get(i));
            if(val<0){
                mStart.multiplyRowByScalar(i,RationalNumber.MINUS_ONE);
                if(types.get(i)==0){
                    types.set(i,2);
                }else if(types.get(i)==2){
                    types.set(i,0);
                }
            }
        }

        int rowNum = mloProblem.getNbRows() + 3;
        int colNum = mloProblem.getNbVar() + 1;
        for(int t : mloProblem.getTypes()){
            switch (t){
                case 0: // <=
                case 1: // =
                    colNum++;
                    break;
                case 2: // >=
                    colNum += 2;
                    break;
            }
        }

        matrix = new RationalNumberMatrix(rowNum,colNum);
        cols = colNum;
        rows = rowNum;

        RationalNumber[] row;
        // we start to fill the matrix with the constraints coefficients
        for(int r=0;r<mloProblem.getNbRows();r++){
            for(int c=0;c<mloProblem.getNbVar();c++){
                matrix.set(r,c,mStart.get(r,c));
            }
            row = mStart.getRow(r);
            matrix.set(r,colNum-1,row[row.length-1]);
        }

        // we add slack, surplus and artificial variables
        int startingCol = mloProblem.getNbVar();
        int nbAddedVar = 0;
        int r = 0;
        for(int t : types){
            switch (t){
                case 0: // <=
                    matrix.set(r,startingCol+nbAddedVar,RationalNumber.ONE);
                    r++;
                    nbAddedVar++;
                    break;
                case 1: // =
                    matrix.set(r,startingCol+nbAddedVar,RationalNumber.ONE);
                    rowsWithArtificialVar.add(r);
                    colsWithArtificialVar.add(startingCol+nbAddedVar);
                    r++;
                    nbAddedVar++;
                    break;
                case 2: // >=
                    matrix.set(r,startingCol+nbAddedVar,RationalNumber.MINUS_ONE);
                    matrix.set(r,startingCol+nbAddedVar+1,RationalNumber.ONE);
                    rowsWithArtificialVar.add(r);
                    colsWithArtificialVar.add(startingCol+nbAddedVar+1);
                    r++;
                    nbAddedVar+=2;
                    break;
            }
        }

        // replace null with 0
        for(r=0;r<mloProblem.getNbRows();r++){
            for(int c=startingCol;c<colNum;c++){
                if(matrix.get(r,c)==null){
                    matrix.set(r,c,RationalNumber.ZERO);
                }
            }
        }

        computeLastRows();
    }

    /**
     * Computes the current matrix.
     * @return IS_OPTIMAL if the solution is optimal, UNBOUNDED if the solution is unbounded, else NOT_OPTIMAL.
     */
    public void compute(){
        System.out.println("Initial matrix:");
        System.out.println(matrix);
        while(!checkOptimality() && !solutionIsUnbounded){
            // find the entering column
            int pivotColumn = findEnteringColumn();
            //System.out.println("Pivot Column: "+pivotColumn);

            // find departing value
            RationalNumber[] ratios = calculateRatios(pivotColumn);
            int pivotRow = findSmallestValue(ratios);
            //System.out.println("Pivot Row: "+pivotRow);

            if(solutionIsUnbounded) {
                System.out.println("UNBOUNDED");
                break; // GOTTA CHANGE THIS
            }else{
                // form the next matrix
                formNextMatrix(pivotRow, pivotColumn);

                if(pivotColumn<nbVariables){
                    colsInBase.add(pivotColumn);
                }

                System.out.println("-------");System.out.println(matrix);
            }
        }
        System.out.println("---Result:---");
        System.out.println(getResult());
    }

    /**
     * Forms a new matrix from precomputed pivot values.
     * @param pivotRow The pivot's row index.
     * @param pivotColumn The pivot's column index.
     */
    private void formNextMatrix(int pivotRow, int pivotColumn){
        RationalNumber pivotValue = matrix.get(pivotRow,pivotColumn);
        RationalNumber[] pivotRowVals = new RationalNumber[cols];
        RationalNumber[] pivotColumnVals = new RationalNumber[rows-3];
        RationalNumber[] rowNew = new RationalNumber[cols];

        // divide all entries in pivot row by entry inpivot column
        // get entry in pivot row
        System.arraycopy(matrix.getRow(pivotRow), 0, pivotRowVals, 0, cols);

        // get entry in pivot column
        for(int i = 0; i < rows-3; i++)
            pivotColumnVals[i] = matrix.get(i,pivotColumn);

        // divide values in pivot row by pivot value
        for(int  i = 0; i < cols; i++)
            rowNew[i] =  pivotRowVals[i].divide(pivotValue);

        // subtract from each of the other rows
        for(int i = 0; i < rows-3; i++){
            if(i != pivotRow){
                for(int j = 0; j < cols; j++){
                    RationalNumber c = pivotColumnVals[i];
                    matrix.set(i,j,matrix.get(i,j).subtract(c.multiply(rowNew[j])));
                }
            }
        }

        // replace the row
        matrix.setRow(pivotRow,rowNew);

        computeLastRows();
    }

    private void computeLastRows(){
        // Zj row
        int rowZj = rows-3;
        for(int c=0;c<cols;c++){
            RationalNumber sum = RationalNumber.ZERO;
            for(int rowWA : rowsWithArtificialVar){
                sum = sum.add(matrix.get(rowWA,c));
            }
            matrix.set(rowZj,c,sum.multiply(RationalNumber.MINUS_ONE));
        }

        // Cj row
        int rowCj = rowZj+1;
        for(int colWA : colsWithArtificialVar){
            matrix.set(rowCj,colWA,RationalNumber.MINUS_ONE);
        }
        for(int c=0;c<cols;c++){
            if(matrix.get(rowCj,c)==null){
                matrix.set(rowCj,c,RationalNumber.ZERO);
            }
        }

        // Zj-Cj row
        int rowZjMinusCj = rowCj+1;
        for(int c=0;c<cols;c++){
            RationalNumber fromZj = matrix.get(rowZj,c);
            RationalNumber fromCj = matrix.get(rowCj,c);
            RationalNumber res = fromZj.subtract(fromCj);
            matrix.set(rowZjMinusCj,c,res);
        }
    }

    /**
     * Calculates the pivot row ratios
     * @param column The pivot's column index.
     * @return The pivot row ratios in an Array.
     */
    private RationalNumber[] calculateRatios(int column){
        RationalNumber[] positiveEntries = new RationalNumber[rows-3];
        RationalNumber[] res = new RationalNumber[rows-3];

        int allNegativeCount = 0;
        for(int i = 0; i < rows-3; i++){
            if(!matrix.get(i,column).isLessThanOrEqualTo(RationalNumber.ZERO)){ //matrix[i][column] > 0
                positiveEntries[i] = matrix.get(i,column);
            }
            else{
                positiveEntries[i] = RationalNumber.ZERO;
                allNegativeCount++;
            }
            //System.out.println(positiveEntries[i]);
        }

        if(allNegativeCount == rows-3)
            this.solutionIsUnbounded = true;
        else{
            for(int i = 0;  i < rows-3; i++){
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
        int location, pos, count=0;
        RationalNumber[] values = new RationalNumber[cols-1];

        for(pos = 0; pos < cols-1; pos++){
            values[pos] = matrix.get(rows-1,pos);
        }

        location = findSmallestValue(values);

        return location;
    }


    /**
     * Finds the smallest value in an array.
     * @param data The array.
     * @return The index of the smallest negative value in the array.
     */
    private int findSmallestValue(RationalNumber[] data){
        RationalNumber minimum ;
        int c, location = 0;
        minimum = data[0];

        for(c = 1; c < data.length; c++){
            if(data[c].isLessThan(minimum)) { //data[c] < minimum
                minimum = data[c];
                location = c;
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
        int c = 0, location = 0;
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
     * Checks if the matrix is optimal
     * @return True if the matrix is optimal, else false.
     */
    public boolean checkOptimality(){
        boolean isOptimal = false;
        int vCount = 0;

        for(int i = 0; i < cols-1; i++){
            RationalNumber val = matrix.get(rows-1,i);
            if(RationalNumber.ZERO.isLessThanOrEqualTo(val)){ // 0 <= val
                vCount++;
            }
        }

        if(vCount == cols-1){
            isOptimal = true;
        }

        return isOptimal;
    }

    public String getResult(){
        StringBuilder str = new StringBuilder();
        str.append("Matrix:\n");
        str.append(matrix.toString());
        str.append("\n");

        RationalNumber[] res = new RationalNumber[nbVariables];
        // variables' values
        for(int i=0;i<nbVariables;i++){
            for(int j=0;j<rows-3;j++){
                if(colsInBase.contains(i)){
                    if(matrix.get(j,i).equals(RationalNumber.ONE)){
                        str.append("Value of var["+i+"] = "+matrix.get(j,cols-1)+"\n");
                        res[i]=matrix.get(j,cols-1);
                    }
                }else{
                    str.append("Value of var["+i+"] = 0\n");
                    j=rows-2;
                    res[i]=RationalNumber.ZERO;
                }
            }
        }

        // check if all the constraints aren't violated
        boolean flag = false;
        for(int r=0;r<rows-3;r++){
            RationalNumber b = new RationalNumber(Double.parseDouble(B.get(r)));
            RationalNumber val = RationalNumber.ZERO;
            for(int v=0;v<nbVariables;v++){
                val = val.add(matrixStart.get(r,v).multiply(res[v]));
            }
            for(int t : types){
                switch(t){
                    case 0: // <=
                        if(!val.isLessThanOrEqualTo(b)) // val <= b
                            flag = true;
                        break;
                    case 1: // =
                        if(!val.equals(b)) // val = b
                            flag = true;
                        break;
                    case 2: // >=
                        if(!b.isLessThanOrEqualTo(val)) // b <= val
                            flag = true;
                        break;
                }
            }
        }

        if(flag){
            str.append("\nThe problem is unfeasible.\n");
        }else{
            str.append("\nThe problem is feasible.\n");
        }

        return str.toString();
    }
}
