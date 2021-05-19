package Model;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raphaël Bagat
 * @version 0.2
 */
public class Solver {
    private MLOProblem mloProblem;

    /**
     * Constructor. This solver minimizes the objective funtion.
     * @param mloProblem The MLO problem to solve.
     */
    public Solver(MLOProblem mloProblem){
        this.mloProblem = mloProblem;
    }

    /**
     * Solves the MLO problem using LP_Solve and prints the results in the standard output.
     */
    public void solveUsingLPSolve(){
        try {
            // Create a problem with 4 variables and 0 constraints
            LpSolve solver = LpSolve.makeLp(0, mloProblem.getNbVar());

            int nbCons = mloProblem.getValues().size();
            int consType;
            // add constraints
            for(int i=0;i<nbCons;i++){
                consType = -1;
                switch(mloProblem.getTypes().get(i)){
                    case 0:
                        consType = LpSolve.LE;
                        break;
                    case 1:
                        consType = LpSolve.EQ;
                        break;
                    case 2:
                        consType = LpSolve.GE;
                        break;
                }
                solver.strAddConstraint(mloProblem.getValues().get(i),consType, Double.parseDouble(mloProblem.getB().get(i)));
            }

            // set objective function
            solver.strSetObjFn(mloProblem.getObjFun());

            // solve the problem
            solver.solve();

            // print solution
            System.out.println("Value of objective function: " + solver.getObjective());
            double[] var = solver.getPtrVariables();
            for (int i = 0; i < var.length; i++) {
                System.out.println("Value of var[" + i + "] = " + var[i]);
            }

            // delete the problem and free memory
            solver.deleteLp();
        }
        catch (LpSolveException e) {
            e.printStackTrace();
        }
    }

    /**
     * Solves the MLO problem using MLO_RB and prints the results in the standard output.
     */
    public void solveUsingMLO_RB(){
        MLO_RB mlo_rb = new MLO_RB(problemToNormalizedProblemMatrix(),mloProblem.getObjFun(),mloProblem.getNbVar());
        mlo_rb.solve();
    }

    public void checkFeasibilityUsingMLO_RB(){
        MLO_RB mlo_rb = new MLO_RB(problemToNormalizedProblemMatrix(),mloProblem.getObjFun(),mloProblem.getNbVar());
        mlo_rb.checkFeasibility(mloProblem.clone());
    }

    /**
     * Converts the problem to a RationalNumberMatrix.
     * @return The problem converted to a RationalNumberMatrix.
     */
    public RationalNumberMatrix problemToNormalizedProblemMatrix(){
        MLOProblem mloProblem = this.mloProblem.clone();
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
            while(countAddedVar<numAddedVariables) {
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

    public RationalNumberMatrix problemToNormalizedPhaseOneMatrix(){
        RationalNumberMatrix mStart = problemToNormalizedProblemMatrix();

        // we want to change all the constraints so that all Bi are positive
        LinkedList<String> B = this.mloProblem.getB();
        LinkedList<Integer> types = this.mloProblem.getTypes();
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

        int rowNum = this.mloProblem.getNbRows() + 3;
        int colNum = this.mloProblem.getNbVar() + 1;
        for(int t : this.mloProblem.getTypes()){
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

        RationalNumberMatrix m = new RationalNumberMatrix(rowNum,colNum);

        RationalNumber[] row;
        // we start to fill the matrix with the constraints coefficients
        for(int r=0;r<this.mloProblem.getNbRows();r++){
            for(int c=0;c<this.mloProblem.getNbVar();c++){
                m.set(r,c,mStart.get(r,c));
            }
            row = mStart.getRow(r);
            m.set(r,colNum-1,row[row.length-1]);
        }

        // we add slack, surplus and artificial variables
        int startingCol = this.mloProblem.getNbVar();
        int nbAddedVar = 0;
        int r = 0;
        LinkedList<Integer> rowsWithArtificialVar = new LinkedList<>();
        LinkedList<Integer> colsWithArtifiacialVar = new LinkedList<>();
        for(int t : types){
            switch (t){
                case 0: // <=
                    m.set(r,startingCol+nbAddedVar,RationalNumber.ONE);
                    r++;
                    nbAddedVar++;
                    break;
                case 1: // =
                    m.set(r,startingCol+nbAddedVar,RationalNumber.ONE);
                    rowsWithArtificialVar.add(r);
                    colsWithArtifiacialVar.add(startingCol+nbAddedVar);
                    r++;
                    nbAddedVar++;
                    break;
                case 2: // >=
                    m.set(r,startingCol+nbAddedVar,RationalNumber.MINUS_ONE);
                    m.set(r,startingCol+nbAddedVar+1,RationalNumber.ONE);
                    rowsWithArtificialVar.add(r);
                    colsWithArtifiacialVar.add(startingCol+nbAddedVar+1);
                    r++;
                    nbAddedVar+=2;
                    break;
            }
        }

        // replace null with 0
        for(r=0;r<this.mloProblem.getNbRows();r++){
            for(int c=startingCol;c<colNum;c++){
                if(m.get(r,c)==null){
                    m.set(r,c,RationalNumber.ZERO);
                }
            }
        }

        // Zj row
        int rowZj = this.mloProblem.getNbRows();
        for(int c=0;c<colNum;c++){
            RationalNumber sum = RationalNumber.ZERO;
            for(int rowWA : rowsWithArtificialVar){
                sum = sum.add(m.get(rowWA,c));
            }
            m.set(rowZj,c,sum.multiply(RationalNumber.MINUS_ONE));
        }

        // Cj row
        int rowCj = rowZj+1;
        for(int colWA : colsWithArtifiacialVar){
            m.set(rowCj,colWA,RationalNumber.MINUS_ONE);
        }
        for(int c=0;c<colNum;c++){
            if(m.get(rowCj,c)==null){
                m.set(rowCj,c,RationalNumber.ZERO);
            }
        }

        // Zj-Cj row
        int rowZjMinusCj = rowCj+1;
        for(int c=0;c<colNum;c++){
            RationalNumber fromZj = m.get(rowZj,c);
            RationalNumber fromCj = m.get(rowCj,c);
            RationalNumber res = fromZj.subtract(fromCj);
            m.set(rowZjMinusCj,c,res);
        }

        return m;
    }
}
