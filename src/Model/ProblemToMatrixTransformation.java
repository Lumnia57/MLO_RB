package Model;

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
     * @return The problem converted to a RationalNumberMatrix.
     */
    public static RationalNumberMatrix problemToNormalizedProblemMatrix(MLOProblem mloProblemP){
        MLOProblem mloProblem = mloProblemP.clone();
        int numAddedVariables = mloProblem.getNbRows();
        int rowNum = mloProblem.getNbRows() + 1;
        int colNum = mloProblem.getNbVar() + 1 + numAddedVariables;
        RationalNumberMatrix matrix = new RationalNumberMatrix(rowNum,colNum);

        LinkedList<String> sl = new LinkedList<>();
        Pattern p;
        Matcher m;

        LinkedList<RationalNumber> rl = new LinkedList<>();
        int num=0;
        int denom;
        int count;
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
            if(index<rowNum-1){
                switch(mloProblem.getTypes().get(index)) {
                    case 0:
                        while (countAddedVar < numAddedVariables) {
                            if (countAddedVar == index)
                                s1 += " 1";
                            else
                                s1 += " 0";
                            countAddedVar++;
                        }
                        break;
                    case 1:
                        while (countAddedVar < numAddedVariables) {
                            s1 += " 0";
                            countAddedVar++;
                        }
                        break;
                    case 2:
                        while (countAddedVar < numAddedVariables) {
                            if (countAddedVar == index)
                                s1 += " -1";
                            else
                                s1 += " 0";
                            countAddedVar++;
                        }
                        break;
                }
            }else{
                while (countAddedVar < numAddedVariables) {
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
            p = Pattern.compile("[-]?[0-9]+");
            count = 0;
            for(String s2 : sl){
                m = p.matcher(s2);
                while (m.find()){
                    if(s2.contains("/")){
                        if(count%2==0){
                            num = Integer.parseInt(m.group(0));
                        }else{
                            denom = Integer.parseInt(m.group(0));
                            rl.addLast(new RationalNumber(num,denom));
                        }
                        count++;
                    }else{
                        num = Integer.parseInt(m.group(0));
                        denom = 1;
                        rl.addLast(new RationalNumber(num,denom));
                    }
                }
            }

            row = rl.toArray(new RationalNumber[rl.size()+1]);
            if(index<rowNum-1){
                row[colNum-1] = new RationalNumber((int)Double.parseDouble(mloProblem.getB().get(index)),1); /* works because B is filled with int */
            }else{
                row[colNum-1] = new RationalNumber(0,1);
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
        int num=0;
        int denom;
        int count;
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
            p = Pattern.compile("[-]?[0-9]+");
            count = 0;
            for(String s2 : sl){
                m = p.matcher(s2);
                while (m.find()){
                    if(s2.contains("/")){
                        if(count%2==0){
                            num = Integer.parseInt(m.group(0));
                        }else{
                            denom = Integer.parseInt(m.group(0));
                            rl.addLast(new RationalNumber(num,denom));
                        }
                        count++;
                    }else{
                        num = Integer.parseInt(m.group(0));
                        denom = 1;
                        rl.addLast(new RationalNumber(num,denom));
                    }
                }
            }

            row = rl.toArray(new RationalNumber[rl.size()+1]);
            if(index<rowNum-1){
                row[colNum-1] = new RationalNumber((int)Double.parseDouble(mloProblem.getB().get(index)),1); /* works because B is filled with int */
            }else{
                row[colNum-1] = new RationalNumber(0,1);
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
