package com.tronography.locationchat.userprofile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Created by jonathancolon on 8/31/17.
 */

class Solution {


    public static void main(String[] args) {

        System.out.println(bouncingBall(57.0, 0.9, 0.57));
    }

    public static int bouncingBall(double h, double bounce, double window) {
        // your code

        System.out.println("h = " + h);
        System.out.println("bounce = " + bounce);
        System.out.println("window = " + window);
        System.out.println(" ");

        double rebound = h * bounce;

        System.out.println("rebound > window = " + String.valueOf(rebound > window));
        System.out.println("window > bounce = " + String.valueOf(window > bounce));
        System.out.println("h > bounce = " + String.valueOf(h > bounce));
        System.out.println("bounce > 0 = " + String.valueOf(bounce > 0));
        System.out.println("bounce < 1 = " + String.valueOf(bounce < 1));

        if (rebound > window && h > bounce && bounce > 0 && bounce < 1) {
            int timesSeen = 1;

            while (rebound > window) {
                rebound = rebound * bounce;
                System.out.println("rebound = " + rebound);
                timesSeen++;
            }
            return (timesSeen * 2) - 1;
        }
        return -1;
    }





    public static int dominator(int[] A) {
        Map<Integer, Integer> mapCount = new HashMap<>();
        int length = A.length;
        int dominator = -1;
        int dominatorIndex = -1;

        if (length >= 1) {
            countOccurences(A, mapCount);
        }

        for (Map.Entry<Integer, Integer> e : mapCount.entrySet()) {
            Integer number = e.getKey();
            Integer count = e.getValue();

            if (count > length / 2) {
                dominator = number;
            }
        }

        if (dominator > -1) {
            for (int i = 0; i < A.length; i++) {
                int number = A[i];
                if (number == dominator) {
                    dominatorIndex = i;
                    return dominatorIndex;
                }
            }
        }

        return dominatorIndex;
    }

    private static void countOccurences(int[] A, Map<Integer, Integer> occurenceMap) {
        for (int number : A) {
            if (occurenceMap.containsKey(number)) {
                occurenceMap.put(number, occurenceMap.get(number) + 1);
            } else {
                occurenceMap.put(number, 1);
            }
        }
    }

    public static int solution(int[] A) {
        // write your code in Java SE 8

        String num = Integer.toBinaryString(9);
        int binaryNumber = Integer.valueOf(num);

        System.out.println(binaryNumber);

        Set<Integer> numSet = new HashSet<>();
        Set<Integer> setA = new HashSet<>();

        for (int i = 0; i < A.length; i++) {
            numSet.add(i + 1);
            setA.add(A[i]);
        }

        System.out.println("setA = " + numSet);

        if (numSet.size() != A.length || setA.size() != numSet.size()) {
            return 0;
        }

        for (int number : A) {
            if (!numSet.contains(number)) {
                return 0;
            }
        }

        return 1;
    }

    public static int binaryGap(int N) {
        // write your code in Java SE 8

        String binaryString = Integer.toBinaryString(N);
        int maxBinaryGap = 0;
        int openingOneIndex;
        int closingOneIndex;
        char currentChar;

        for (int i = 0; i < binaryString.length(); i++) {

            currentChar = binaryString.charAt(i);

            if (currentChar == '1') {

                if (i + 1 < binaryString.length()) {

                    openingOneIndex = i;
                    closingOneIndex = binaryString.indexOf('1', i + 1);

                    int numberOfZeros = closingOneIndex - openingOneIndex;
                    if (maxBinaryGap < numberOfZeros) {
                        maxBinaryGap = numberOfZeros - 1;
                    }

                }
            }
        }
        return maxBinaryGap;
    }

    public static int oddOccurence(int[] A) {
        // write your code in Java SE 8
        Map<Integer, Integer> occurenceMap = new HashMap<>();

        int oddOcccurence = -1;

        countOccurences(A, occurenceMap);
        oddOcccurence = findOddOccurence(occurenceMap, oddOcccurence);

        return oddOcccurence;
    }

    private static int findOddOccurence(Map<Integer, Integer> occurenceMap, int oddOcccurence) {
        for (Map.Entry<Integer, Integer> e : occurenceMap.entrySet()) {
            Integer number = e.getKey();
            Integer count = e.getValue();

            if (count % 2 != 0) {
                oddOcccurence = number;
            }
        }
        return oddOcccurence;
    }

    public static boolean canPushToStack(int x) {
        return x >= 0 && x < Math.pow(2, 20);
    }

    public int permMissingEle(int[] A) {
        // write your code in Java SE 8
        Arrays.sort(A);

        int currentElement;
        int nextElement;
        int missingElement = -1;
        int length = A.length;

        switch (length) {

            case (0):
                missingElement = 1;
                break;
            case (1):
                if (A[0] != 1) {
                    missingElement = 1;
                } else {
                    missingElement = A[length - 1] + 1;
                }

                break;
            default:
                for (int i = 0; i < length - 1; i++) {
                    currentElement = A[i];
                    nextElement = A[i + 1];

                    if (A[0] != 1) {
                        missingElement = 1;
                        break;
                    }

                    if (currentElement + 1 != nextElement) {
                        missingElement = currentElement + 1;
                        break;
                    }

                    missingElement = A[length - 1] + 1;
                }
        }

        return missingElement;
    }

    public int[] rotateArray(int[] A, int K) {

        // write your code in Java SE 8

        if (A.length == 0) {
            return A;
        }

        int offset = K % A.length;
        int count = 0;

        for (int start = 0; count < A.length; start++) {
            int current = start;
            int prev = A[start];
            do {
                int next = (current + offset) % A.length;
                int temp = A[next];
                A[next] = prev;
                prev = temp;
                current = next;
                count++;
            } while (start != current);
        }
        return A;
    }

    public int properFormat(String string) {
        Stack<Character> stack = new Stack<>();
        int isInvalid = 0;
        int isValid = 1;

        char currentChar;
        for (int i = 0; i < string.length(); i++) {
            currentChar = string.charAt(i);

            if (currentChar == '{' || currentChar == '[' || currentChar == '(') {
                stack.push(currentChar);
            }

            if (currentChar == '}' || currentChar == ']' || currentChar == ')') {

                if (stack.isEmpty()) {
                    return isInvalid;
                } else if (!isMatchingPair(stack.pop(), currentChar)) {
                    return isInvalid;
                }
            }
        }


        if (stack.isEmpty()) {
            return isValid;
        }

        return isInvalid;
    }

    static boolean isMatchingPair(char character1, char character2) {
        if (character1 == '(' && character2 == ')')
            return true;
        else if (character1 == '{' && character2 == '}')
            return true;
        else if (character1 == '[' && character2 == ']')
            return true;
        else
            return false;
    }
}
