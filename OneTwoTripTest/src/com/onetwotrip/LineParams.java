package com.onetwotrip;

import java.util.HashMap;
import java.util.Map;

public class LineParams {

    private final boolean caseSensitive;
    private final Map<Character, SymbolParams> stringParams;

    private final int originalLineSize;

    private int numbersSymbolParams = 0;

    public LineParams(boolean caseSensitive, String line) {
        this.caseSensitive = caseSensitive;
        this.stringParams = new HashMap<>();

        this.originalLineSize = line.length();

        initWithLine(line);
    }

    private void initWithLine(String line) {
        for (int i = 0; i < line.length(); i++) {
            final Character originalChar = line.charAt(i);
            final Character comparableChar = caseSensitive ? originalChar : Character.toLowerCase(originalChar);
            if (stringParams.containsKey(comparableChar)) {
                stringParams.get(comparableChar).addParams(originalChar, i);
            } else {
                stringParams.put(comparableChar, new SymbolParams(originalChar, i));
            }
        }
    }

    public boolean addPositionIfNeeded(int row, int column, char ch) {
        Character charToFind = caseSensitive ? ch : Character.toLowerCase(ch);
        if (stringParams.containsKey(charToFind)) {
            final SymbolParams symbolParams = stringParams.get(charToFind);
            if (symbolParams.hasEmptyPositions()) {
                symbolParams.addPosition(row, column);
                numbersSymbolParams++;
            }
        }
        return numbersSymbolParams == originalLineSize;
    }

    @Override
    public String toString() {
        final String[] outputLines = new String[originalLineSize];
        for (Character keyChar : stringParams.keySet()) {
            final SymbolParams symbolParams = stringParams.get(keyChar);
            for (int i = 0; i < symbolParams.size(); i++) {
                outputLines[symbolParams.getOriginalIndexAt(i)] = symbolParams.toString(i);
            }
        }

        final StringBuilder result = new StringBuilder();
        for (String outputLine : outputLines) {
            result.append(outputLine);
            result.append("\n");
        }

        return result.toString();
    }
}
