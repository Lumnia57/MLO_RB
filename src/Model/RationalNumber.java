package Model;
//********************************************************************
//  RationalNumber.java       Author: Lewis/Loftus
//
//  Represents one rational number with a numerator and denominator.
//********************************************************************

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RationalNumber
{
    private int numerator, denominator;
    public static RationalNumber ZERO = new RationalNumber(0,1);
    public static RationalNumber ONE = new RationalNumber(1,1);
    public static RationalNumber MINUS_ONE = new RationalNumber(-1,1);

    //-----------------------------------------------------------------
    //  Constructor: Sets up the rational number by ensuring a nonzero
    //  denominator and making only the numerator signed.
    //-----------------------------------------------------------------
    public RationalNumber (int numer, int denom)
    {
        if (numer == 0)
            denom = 1;

        if (denom == 0)
            denom = 1;

        // Make the numerator "store" the sign
        if (denom < 0)
        {
            numer = numer * -1;
            denom = denom * -1;
        }

        numerator = numer;
        denominator = denom;

        reduce();
    }

    public RationalNumber (double d){
        denominator = 1;
        while(d%1!=0){
            d*=10;
            denominator *= 10;
        }
        numerator = (int)d;

        reduce();
    }

    public RationalNumber(String s){
        Pattern p;
        Matcher m;
        p = Pattern.compile("[-]?[0-9]+");
        m = p.matcher(s);
        int count = 0;
        while (m.find()){
            if(s.contains("/")){
                if(count%2==0){
                    numerator = Integer.parseInt(m.group(0));
                }else{
                    denominator = Integer.parseInt(m.group(0));
                }
                count++;
            }else{
                numerator = Integer.parseInt(m.group(0));
                denominator = 1;
            }
        }

        reduce();
    }

    //-----------------------------------------------------------------
    //  Returns the numerator of this rational number.
    //-----------------------------------------------------------------
    public int getNumerator ()
    {
        return numerator;
    }

    //-----------------------------------------------------------------
    //  Returns the denominator of this rational number.
    //-----------------------------------------------------------------
    public int getDenominator ()
    {
        return denominator;
    }

    //-----------------------------------------------------------------
    //  Returns the reciprocal of this rational number.
    //-----------------------------------------------------------------
    public RationalNumber reciprocal ()
    {
        return new RationalNumber (denominator, numerator);
    }

    //-----------------------------------------------------------------
    //  Adds this rational number to the one passed as a parameter.
    //  A common denominator is found by multiplying the individual
    //  denominators.
    //-----------------------------------------------------------------
    public RationalNumber add (RationalNumber op2)
    {
        int commonDenominator = denominator * op2.getDenominator();
        int numerator1 = numerator * op2.getDenominator();
        int numerator2 = op2.getNumerator() * denominator;
        int sum = numerator1 + numerator2;

        return new RationalNumber (sum, commonDenominator);
    }

    //-----------------------------------------------------------------
    //  Subtracts the rational number passed as a parameter from this
    //  rational number.
    //-----------------------------------------------------------------
    public RationalNumber subtract (RationalNumber op2)
    {
        int commonDenominator = denominator * op2.getDenominator();
        int numerator1 = numerator * op2.getDenominator();
        int numerator2 = op2.getNumerator() * denominator;
        int difference = numerator1 - numerator2;

        return new RationalNumber (difference, commonDenominator);
    }

    //-----------------------------------------------------------------
    //  Multiplies this rational number by the one passed as a
    //  parameter.
    //-----------------------------------------------------------------
    public RationalNumber multiply (RationalNumber op2)
    {
        int numer = numerator * op2.getNumerator();
        int denom = denominator * op2.getDenominator();

        return new RationalNumber (numer, denom);
    }

    //-----------------------------------------------------------------
    //  Divides this rational number by the one passed as a parameter
    //  by multiplying by the reciprocal of the second rational.
    //-----------------------------------------------------------------
    public RationalNumber divide (RationalNumber op2)
    {
        return multiply (op2.reciprocal());
    }

    //-----------------------------------------------------------------
    //  Determines if this rational number is equal to the one passed
    //  as a parameter.  Assumes they are both reduced.
    //-----------------------------------------------------------------
    public boolean equals (RationalNumber op2)
    {
        return ( numerator == op2.getNumerator() &&
                denominator == op2.getDenominator() );
    }

    //-----------------------------------------------------------------
    //  Returns this rational number as a string.
    //-----------------------------------------------------------------
    public String toString ()
    {
        String result;

        if (numerator == 0)
            result = "0";
        else
        if (denominator == 1)
            result = numerator + "";
        else
            result = numerator + "/" + denominator;

        return result;
    }

    //-----------------------------------------------------------------
    //  Reduces this rational number by dividing both the numerator
    //  and the denominator by their greatest common divisor.
    //-----------------------------------------------------------------
    private void reduce ()
    {
        if (numerator != 0)
        {
            int common = gcd (Math.abs(numerator), denominator);

            numerator = numerator / common;
            denominator = denominator / common;
        }
    }

    //-----------------------------------------------------------------
    //  Computes and returns the greatest common divisor of the two
    //  positive parameters. Uses Euclid's algorithm.
    //-----------------------------------------------------------------
    private int gcd (int num1, int num2)
    {
        while (num1 != num2)
            if (num1 > num2)
                num1 = num1 - num2;
            else
                num2 = num2 - num1;

        return num1;
    }

    public boolean isLessThanOrEqualTo(RationalNumber op2){
        boolean res = false;
        if(this.numerator<0 && op2.numerator>=0)
            res = true;
        if(this.numerator>=0 && op2.numerator>=0 || this.numerator<=0 && op2.numerator<=0){
            int numerator1 = this.numerator*op2.denominator;
            int numerator2 = op2.numerator*this.denominator;
            res = numerator1<=numerator2;
        }

        return res;
    }

    public boolean isLessThan(RationalNumber op2){
        boolean res = false;
        if(this.numerator<0 && op2.numerator>=0)
            res = true;
        if(this.numerator>=0 && op2.numerator>=0 || this.numerator<=0 && op2.numerator<=0){
            int numerator1 = this.numerator*op2.denominator;
            int numerator2 = op2.numerator*this.denominator;
            res = numerator1<numerator2;
        }

        return res;
    }

    public RationalNumber absoluteValue(){
        int newNum = numerator;
        if(newNum<0)
            newNum *= -1;
        return new RationalNumber(newNum,denominator);
    }

    @Override
    public RationalNumber clone(){
        return new RationalNumber(numerator,denominator);
    }

    public double toDouble(){
        return (double)numerator/(double)denominator;
    }
}