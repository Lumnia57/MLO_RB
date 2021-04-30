package Model;

/**
 * @author Raphaël Bagat
 * @version 1.0
 */
public class RationalNumberMatrix {
    RationalNumber[][] matrix;
    int rowNum;
    int colNum;

    /**
     * Constructor.
     * @param rowNum The matrix's number of rows.
     * @param colNum The matrix's number of columns.
     * @throws AssertionError Both parameters have to be positive.
     */
    public RationalNumberMatrix(int rowNum, int colNum){
        assert(rowNum>0 && colNum>0):"Row and column numbers have to be greater than 0";
        matrix = new RationalNumber[rowNum][colNum];
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    /**
     * Adds a row in the matrix.
     * @param newRow The row to add in the matrix.
     * @param rowIndex The row index to add the new row.
     * @throws AssertionError rowIndex has to be less than rowNum.
     * @throws AssertionError New row's length has to be equal to the matrix's rows' length.
     */
    public void addRow(RationalNumber[] newRow, int rowIndex){
        assert(newRow.length==colNum):"New row's length has to be equal to the matrix's rows' length";
        assert(rowIndex<rowNum):"Row index out of bounds";
        matrix[rowIndex]=newRow.clone();
    }

    /**
     * Addition operation.
     * @param other The other matrix to add to this matrix.
     * @return The result of the addition of the two matrices.
     * @throws AssertionError The two matrices must have the same dimensions.
     */
    public RationalNumberMatrix add(RationalNumberMatrix other){
        assert(rowNum==other.rowNum && colNum==other.colNum):"The two matrices must have the same dimensions";
        RationalNumberMatrix newMatrix = new RationalNumberMatrix(rowNum,colNum);
        RationalNumber[] newRow = new RationalNumber[colNum];
        for(int i = 0; i<rowNum; i++){
            for(int j = 0; j<colNum; j++){
                newRow[j] = matrix[i][j].add(other.matrix[i][j]);
            }
            newMatrix.addRow(newRow,i);
        }
        return newMatrix;
    }

    /**
     * Substracting operation.
     * @param other The other matrix to substract to this matrix.
     * @return The result of the substraction of the two matrices (C = this - other).
     * @throws AssertionError The two matrices must have the same dimensions.
     */
    public RationalNumberMatrix substract(RationalNumberMatrix other){
        assert(rowNum==other.rowNum && colNum==other.colNum):"The two matrices must have the same dimensions";
        RationalNumberMatrix newMatrix = new RationalNumberMatrix(rowNum,colNum);
        RationalNumber[] newRow = new RationalNumber[colNum];
        for(int i = 0; i<rowNum; i++){
            for(int j = 0; j<colNum; j++){
                newRow[j] = matrix[i][j].subtract(other.matrix[i][j]);
            }
            newMatrix.addRow(newRow,i);
        }
        return newMatrix;
    }

    /**
     * Matrix product.
     * @param other The other matrix to multiply to this matrix.
     * @return The result of the product of the two matrices. (C = this . other)
     * @throws AssertionError This matrix's number of columns has to be equal to the other matrix's number of rows.
     */
    public RationalNumberMatrix multiply(RationalNumberMatrix other){
        assert(colNum==other.rowNum):"This matrix's number of columns has to be equal to the other matrix's number of rows";
        RationalNumberMatrix newMatrix = new RationalNumberMatrix(rowNum,other.colNum);
        RationalNumber[] newRow = new RationalNumber[other.colNum];
        for(int i=0;i<rowNum;i++){
            initZero(newRow);
            for(int j=0;j<other.colNum;j++){
                for(int k=0;k<colNum;k++){
                    newRow[j] = newRow[j].add(matrix[i][k].multiply(other.matrix[k][j]));
                }
            }
            newMatrix.addRow(newRow,i);
        }
        return newMatrix;
    }

    /**
     * Returns the element at the specified position in this matrix.
     * @param row The row index of the element to return.
     * @param col The column index of the element to return.
     * @return The element at the specified position in this matrix.
     * @throws AssertionError If the index is out of range.
     */
    public RationalNumber get(int row,int col){
        assert(row<rowNum && col<colNum):"";
        return matrix[row][col];
    }

    /**
     * Returns this matrix in a 2-dimensional array. Each elements of the array are of double type instead.
     * @return This matrix in a 2-dimensional array. Each elements of the array are of double type instead.
     */
    public double[][] toDouble(){
        double[][] result = new double[rowNum][colNum];
        for(int i = 0; i<rowNum; i++){
            for(int j = 0; j<colNum; j++){
                result[i][j]=(double)matrix[i][j].getNumerator()/matrix[i][j].getDenominator();
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        for(int i = 0; i<rowNum; i++){
            for(int j = 0; j<colNum; j++){
                strb.append(matrix[i][j]+" ");
            }
            strb.deleteCharAt(strb.length()-1);
            strb.append("\n");
        }
        strb.deleteCharAt(strb.length()-1);
        return strb.toString();
    }

    /**
     * Initialize each elements of the parameter to zero.
     * @param row The array to initialize.
     */
    private void initZero(RationalNumber[] row){
        for(int i=0;i<row.length;i++){
            row[i] = new RationalNumber(0,1);
        }
    }

    @Override
    public RationalNumberMatrix clone(){
        RationalNumberMatrix m = new RationalNumberMatrix(rowNum,colNum);
        m.matrix = matrix.clone();
        return m;
    }
}
