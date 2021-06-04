package Model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raphaël Bagat
 * @version 1.0
 */
public class ProblemToMatrixTransformation {
    /**
     * Converts the problem to a RationalNumberMatrix.
     * @param mloProblemP The MLO problem.
     * @return The problem converted to a RationalNumberMatrix.
     */
    public static RationalNumberMatrix problemToNormalizedProblemMatrix(MLOProblem mloProblemP){
        MLOProblem mloProblem = mloProblemP.clone();
        int numSlackVariables = mloProblem.getNbRows();
        int rowNum = mloProblem.getNbRows() + 1;
        int numNotLowerBoundedVariables = 0;
        if(mloProblem.getNotLowerBoundedVariableIndexes()!=null){
            numNotLowerBoundedVariables = mloProblem.getNotLowerBoundedVariableIndexes().length;
        }
        int colNum = mloProblem.getNbVar() + numNotLowerBoundedVariables + 1 + numSlackVariables;
        RationalNumberMatrix matrix = new RationalNumberMatrix(rowNum,colNum);

        LinkedList<String> sl = new LinkedList<>();
        Pattern p;
        Matcher m;

        LinkedList<RationalNumber> rl = new LinkedList<>();
        int countAddedVar;

        int index = 0;
        RationalNumber[] row;

        LinkedList<String> toBeConverted = mloProblem.getValues();
        toBeConverted.addLast(mloProblem.getObjFun());

        /* row string to matrix */
        for(String s1 : toBeConverted){
            rl.clear();
            sl.clear();
            countAddedVar=0;
            // add slack variables
            if(index<rowNum-1){
                switch(mloProblem.getTypes().get(index)) {
                    case 0: // <=
                        while (countAddedVar < numSlackVariables) {
                            if (countAddedVar == index)
                                s1 += " 1";
                            else
                                s1 += " 0";
                            countAddedVar++;
                        }
                        break;
                    case 1: // =
                        while (countAddedVar < numSlackVariables) {
                            s1 += " 0";
                            countAddedVar++;
                        }
                        break;
                    case 2: // >=
                        while (countAddedVar < numSlackVariables) {
                            if (countAddedVar == index)
                                s1 += " -1";
                            else
                                s1 += " 0";
                            countAddedVar++;
                        }
                        break;
                }
            }else{
                while (countAddedVar < numSlackVariables) {
                    s1 += " 0";
                    countAddedVar++;
                }
            }

            /* row string to string list */
            p = Pattern.compile("[-]?[0-9]+([/][-]?[0-9]+)?");
            m = p.matcher(s1);
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

            row = rl.toArray(new RationalNumber[rl.size()+1]);
            if(index<rowNum-1){ // if it's not the last row, we get the right hand side value
                row[colNum-1] = new RationalNumber(mloProblem.getB().get(index));
            }else{
                row[colNum-1] = RationalNumber.ZERO;
            }

            matrix.addRow(row,index);

            if(index<rowNum-1){
                if(mloProblem.getTypes().get(index)==2){
                    matrix.multiplyRowByScalar(index,RationalNumber.MINUS_ONE);
                }
            }

            index++;
        }

        // we want to minimize so we have to change the sign of the objective function's ratios
        for(int i=0;i<matrix.getColNum();i++){
            matrix.set(matrix.getRowNum()-1,i,matrix.get(matrix.getRowNum()-1,i).multiply(RationalNumber.MINUS_ONE));
        }

        return matrix;
    }

    /**
     * Converts the problem to its dual problem's RationalNumberMatrix.
     * @param mloProblemP The MLO problem.
     * @return The problem converted to its dual problem's RationalNumberMatrix.
     */
    public static RationalNumberMatrix problemToDualProblemMatrix(MLOProblem mloProblemP){
        RationalNumberMatrix primalMatrix = problemToNormalizedProblemMatrix(mloProblemP.clone());
        MLOProblem mloProblem = mloProblemP.clone();

        // converts <= in primal to >=
        for(int i=0;i<primalMatrix.getRowNum()-1;i++){
            primalMatrix.multiplyRowByScalar(i,RationalNumber.MINUS_ONE);
        }

        // dual problem data
        int nbVar = primalMatrix.getRowNum()-1;
        int nbCons = mloProblem.getNbVar();

        RationalNumberMatrix matrix = new RationalNumberMatrix(nbCons+1,nbVar+nbCons+1);

        // dual problem's contrainsts
        for(int c=0;c<(primalMatrix.getColNum()-1)/2;c++){
            for(int r=0;r<primalMatrix.getRowNum()-1;r++){
                matrix.set(c,r,primalMatrix.get(r,c));
            }
        }

        // dual problem's objective function
        for(int c=0;c<mloProblem.getNbRows();c++){
            matrix.set(nbCons,c,primalMatrix.get(c,primalMatrix.getColNum()-1));
        }

        //dual problem's right hand side's values
        for(int r=0;r<nbCons;r++){
            matrix.set(r,nbVar+nbCons,primalMatrix.get(primalMatrix.getRowNum()-1,r));
        }

        // fill the last row with 0s
        for(int c=nbVar;c<matrix.getColNum();c++){
            matrix.set(nbCons,c,RationalNumber.ZERO);
        }

        // add the slack variables
        for(int c=nbVar;c<matrix.getColNum()-1;c++){
            for(int r=0;r<nbCons;r++){
                if(c-nbVar==r){
                    matrix.set(r,c,RationalNumber.ONE);
                }else{
                    matrix.set(r,c,RationalNumber.ZERO);
                }
            }
        }

        // multiply the last column by -1
        matrix.multiplyColumnByScalar(matrix.getColNum()-1,RationalNumber.MINUS_ONE);

        return matrix;
    }

    /**
     * Converts the problem to a RationalNumberMatrix to be used in the Phase One simplex algorithm for MLO_RB.
     * @return The problem converted to a RationalNumberMatrix.
     */
    public static RationalNumberMatrix problemToNormalizedProblemMatrixForPhaseOne(MLOProblem mloProblemP){
        MLOProblem mloProblem = mloProblemP.clone();
        int numAddedVariables = mloProblem.getNbRows();
        int rowNum = mloProblem.getNbRows() + 1;
        int colNum = mloProblem.getNbVar() + 1 + numAddedVariables;
        RationalNumberMatrix matrix = new RationalNumberMatrix(rowNum,colNum);

        LinkedList<String> sl = new LinkedList<>();
        Pattern p;
        Matcher m;

        LinkedList<RationalNumber> rl = new LinkedList<>();
        int countAddedVar;

        int index = 0;
        RationalNumber[] row;

        LinkedList<String> toBeConverted = mloProblem.getValues();
        toBeConverted.addLast(mloProblem.getObjFun());

        /* row string to matrix */
        for(String s1 : toBeConverted){
            rl.clear();
            sl.clear();
            countAddedVar=0;
            while (countAddedVar < numAddedVariables) {
                if (countAddedVar == index)
                    s1 += " 1";
                else
                    s1 += " 0";
                countAddedVar++;
            }

            /* row string to string list */
            p = Pattern.compile("[-]?[0-9]+([/][-]?[0-9]+)?");
            m = p.matcher(s1);
            while (m.find()){
                sl.addLast(m.group(0));
            }

            /* string list to rational number list */
            for(String s2 : sl){
                rl.addLast(new RationalNumber(s2));
            }

            row = rl.toArray(new RationalNumber[rl.size()+1]);
            if(index<rowNum-1){
                row[colNum-1] = new RationalNumber(mloProblem.getB().get(index));
            }else{
                row[colNum-1] = RationalNumber.ZERO;
            }

            matrix.addRow(row,index);

            index++;
        }

        // we want to minimize so we have to change the sign of the objective function's ratios
        for(int i=0;i<matrix.getColNum();i++){
            matrix.set(matrix.getRowNum()-1,i,matrix.get(matrix.getRowNum()-1,i).multiply(RationalNumber.MINUS_ONE));
        }

        return matrix;
    }
}
