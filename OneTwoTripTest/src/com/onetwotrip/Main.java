package com.onetwotrip;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        final LineParams lineParams = new LineParams(readCaseSensitive(), readLineToSearch());

        final Matrix matrix = readCharMatrixFromInputFile();
        for (int row = 0; row < matrix.rowCount; row++) {
            for (int column = 0; column < matrix.columnCount; column++) {
                if (lineParams.addPositionIfNeeded(row, column, matrix.matrix[row][column])) {
                    break;
                }
            }
        }

        try(PrintWriter out = new PrintWriter(readOutputFile())  ){
            out.println(lineParams);
        } catch (Exception ignored) {
        }
    }

    private static Matrix readCharMatrixFromInputFile() {
        final Scanner consoleScanner = new Scanner(System.in);
        Matrix matrix = showAndReadInputMatrixFromInputFile(consoleScanner);
        while (matrix == null) {
            matrix = showAndReadInputMatrixFromInputFile(consoleScanner);
        }
        return matrix;
    }

    private static boolean readCaseSensitive() {
        final Scanner consoleScanner = new Scanner(System.in);
        Boolean caseSensitive = showAndReadCaseSensitive(consoleScanner);
        while (caseSensitive == null) {
            caseSensitive = showAndReadCaseSensitive(consoleScanner);
        }
        return caseSensitive;
    }

    private static Boolean showAndReadCaseSensitive(Scanner consoleScanner) {
        System.out.print("Do you want to search with case sensitive?(Y/N):");
        String caseSensitiveString = consoleScanner.next();
        switch (caseSensitiveString) {
            case "Y":
            case "y":
                return true;
            case "N":
            case "n":
                return false;
            default:
                return null;
        }
    }

    private static String readLineToSearch() {
        final Scanner consoleScanner = new Scanner(System.in);
        System.out.print("Enter a line you want to search:");
        return consoleScanner.next();
    }

    private static Matrix showAndReadInputMatrixFromInputFile(Scanner consoleScanner) {
        System.out.print("Enter path to correct input file:");
        String inputFilePath = consoleScanner.next();

        try (Scanner scanner = new Scanner(new File(inputFilePath))) {
            Matrix matrix = new Matrix(scanner.nextInt(), scanner.nextInt());
            for (int i = 0; i < matrix.rowCount; i++) {
                char[] chs = scanner.next().toCharArray();
                System.arraycopy(chs, 0, matrix.matrix[i], 0, matrix.columnCount);
            }
            return matrix;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String readOutputFile() {
        final Scanner consoleScanner = new Scanner(System.in);
        System.out.print("Enter path to output file:");
        return consoleScanner.next();
    }
}
