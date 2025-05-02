// package ch01.sec01;

/**
 * This is Javadoc notation
 */

public class HelloWorld {
    /**
     *
     * @param args describe what this variable is
     */
    public static void main(String[] args) {
        long aLong = 10L; // Won't need extra time to change a literal int (10) to a long
        short aShort = 10;
        byte aByte = 10;

        float aFloat = 2.5F; // Need an "F/f" after the num
        double aDouble = 1.0/3.0;

        char char1 = 'a';
        char char2 = '\u03A3';

        boolean bool1 = true;
        boolean bool2 = false;

        System.out.println(aLong + ", " + aShort + ", " + aByte);
        System.out.printf("%d, %d, %d\n", aLong, aShort, aByte);

        System.out.println(aFloat + ", " + aDouble);
        System.out.printf("%.2f, %.2f\n", aFloat, aDouble); // .2 specifies the number of digits

        System.out.println(char1 + ", " + char2);
        System.out.printf("%c, %c\n", char1, char2);

        System.out.println(bool1 + ", " + bool2);
        System.out.printf("%b, %b\n", bool1, bool2);

        /*
         String Commands:
         - length()
         - charAt()
         - trim()
         - startsWith(String)
         - indexOf(int)
         - indexOf(String)
         - substring(int)
         - substring(int, int)
         - Builder:
            - StringBuilder builder = new StringBuilder();
            - builder.append(String);
            - String str = builder.toString();
         */

        /*
        Arrays:
        - int[] intArray // Reference
        - intArray = new int[10] // Create actual array
        - for (int value : intArray) {
            System.out.print(value);
            System.out.print(", ");
        }
        - char[][] table = new char[?][?]; // If last left blank, can specify individually the size of each internal array
         */

        System.out.println("Hello, World!");
    }
}