package test;

import Model.RationalNumber;
import Model.RationalNumberMatrix;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RationalNumberMatrixTest {
    @Test
    public void testToString(){
        RationalNumberMatrix matrix = new RationalNumberMatrix(4,2);
        RationalNumber[] arr0 = {new RationalNumber(12,5),new RationalNumber(20,3)};
        RationalNumber[] arr1 = {new RationalNumber(8,7),new RationalNumber(-4,6)};
        RationalNumber[] arr2 = {new RationalNumber(7,8),new RationalNumber(1,-6)};
        RationalNumber[] arr3 = {new RationalNumber(4,7),new RationalNumber(64,16)};
        matrix.addRow(arr0,0);
        matrix.addRow(arr1,1);
        matrix.addRow(arr2,2);
        matrix.addRow(arr3,3);
        String expected =
                "12/5 20/3\n" +
                "8/7 -2/3\n" +
                "7/8 -1/6\n" +
                "4/7 4"
                ;
        assertEquals(matrix.toString(),expected);
    }

    @Test
    public void testAdd(){
        RationalNumberMatrix matrix1 = new RationalNumberMatrix(4,2);
        RationalNumber[] arr10 = {new RationalNumber(12,5),new RationalNumber(20,3)};
        RationalNumber[] arr11 = {new RationalNumber(8,7),new RationalNumber(-4,6)};
        RationalNumber[] arr12 = {new RationalNumber(7,8),new RationalNumber(1,-6)};
        RationalNumber[] arr13 = {new RationalNumber(4,7),new RationalNumber(64,16)};
        matrix1.addRow(arr10,0);
        matrix1.addRow(arr11,1);
        matrix1.addRow(arr12,2);
        matrix1.addRow(arr13,3);

        RationalNumberMatrix matrix2 = new RationalNumberMatrix(4,2);
        RationalNumber[] arr20 = {new RationalNumber(16,6),new RationalNumber(78,65)};
        RationalNumber[] arr21 = {new RationalNumber(4,6),new RationalNumber(7,-9)};
        RationalNumber[] arr22 = {new RationalNumber(23,4),new RationalNumber(63,21)};
        RationalNumber[] arr23 = {new RationalNumber(6,2),new RationalNumber(3,49)};
        matrix2.addRow(arr20,0);
        matrix2.addRow(arr21,1);
        matrix2.addRow(arr22,2);
        matrix2.addRow(arr23,3);

        String expected =
                "76/15 118/15\n" +
                "38/21 -13/9\n" +
                "53/8 17/6\n" +
                "25/7 199/49"
                ;

        RationalNumberMatrix result1 = matrix1.add(matrix2);
        RationalNumberMatrix result2 = matrix2.add(matrix1);

        assertEquals(result1.toString(),expected);
        assertEquals(result2.toString(),expected);
    }

    @Test
    public void testSubstract(){
        RationalNumberMatrix matrix1 = new RationalNumberMatrix(4,2);
        RationalNumber[] arr10 = {new RationalNumber(12,5),new RationalNumber(20,3)};
        RationalNumber[] arr11 = {new RationalNumber(8,7),new RationalNumber(-4,6)};
        RationalNumber[] arr12 = {new RationalNumber(7,8),new RationalNumber(1,-6)};
        RationalNumber[] arr13 = {new RationalNumber(4,7),new RationalNumber(64,16)};
        matrix1.addRow(arr10,0);
        matrix1.addRow(arr11,1);
        matrix1.addRow(arr12,2);
        matrix1.addRow(arr13,3);

        RationalNumberMatrix matrix2 = new RationalNumberMatrix(4,2);
        RationalNumber[] arr20 = {new RationalNumber(16,6),new RationalNumber(78,65)};
        RationalNumber[] arr21 = {new RationalNumber(4,6),new RationalNumber(7,-9)};
        RationalNumber[] arr22 = {new RationalNumber(23,4),new RationalNumber(63,21)};
        RationalNumber[] arr23 = {new RationalNumber(6,2),new RationalNumber(3,49)};
        matrix2.addRow(arr20,0);
        matrix2.addRow(arr21,1);
        matrix2.addRow(arr22,2);
        matrix2.addRow(arr23,3);

        String expected1 =
                "-4/15 82/15\n" +
                "10/21 1/9\n" +
                "-39/8 -19/6\n" +
                "-17/7 193/49"
                ;

        String expected2 =
                "4/15 -82/15\n" +
                "-10/21 -1/9\n" +
                "39/8 19/6\n" +
                "17/7 -193/49"
                ;

        RationalNumberMatrix result1 = matrix1.substract(matrix2);
        RationalNumberMatrix result2 = matrix2.substract(matrix1);

        assertEquals(result1.toString(),expected1);
        assertEquals(result2.toString(),expected2);
    }

    @Test
    public void testMultiply() {
        RationalNumberMatrix matrix1 = new RationalNumberMatrix(2,3);
        RationalNumber[] arr10 = {new RationalNumber(12,5),new RationalNumber(20,3),new RationalNumber(31,23)};
        RationalNumber[] arr11 = {new RationalNumber(8,7),new RationalNumber(-4,6),new RationalNumber(-5,8)};
        matrix1.addRow(arr10,0);
        matrix1.addRow(arr11,1);

        RationalNumberMatrix matrix2 = new RationalNumberMatrix(3,2);
        RationalNumber[] arr20 = {new RationalNumber(16,6),new RationalNumber(78,65)};
        RationalNumber[] arr21 = {new RationalNumber(4,6),new RationalNumber(7,-9)};
        RationalNumber[] arr22 = {new RationalNumber(23,4),new RationalNumber(63,21)};
        matrix2.addRow(arr20,0);
        matrix2.addRow(arr21,1);
        matrix2.addRow(arr22,2);

        RationalNumberMatrix result1 = matrix1.multiply(matrix2);
        RationalNumberMatrix result2 = matrix2.multiply(matrix1);

        String expected1 =
                "3347/180 26987/15525\n" +
                "-1997/2016 113/7560"
                ;

        String expected2 =
                "272/35 764/45 785/276\n" +
                "32/45 134/27 2293/1656\n" +
                "603/35 109/3 47/8"
                ;

        assertEquals(result1.toString(),expected1);
        assertEquals(result2.toString(),expected2);
    }
}