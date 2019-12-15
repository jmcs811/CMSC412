package com.jcaseydev;

////////////////////////
// Author: Justin Casey
// Date: 12 Dec 2019
//

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {

  static final int MIN_VIRTUAL_FRAME = 0;
  static final int MAX_VIRTUAL_FRAME = 9;
  static final int MIN_PHYSICAL_FRAME = 1;
  static final int MAX_PHYSICAL_FRAME = 8;

  static ArrayList<Integer> referenceString = new ArrayList<>();

  private static void processInput(int option, Scanner input) throws IOException {
    switch (option) {
      case 0:
        System.exit(1);
        break;
      case 1:
        //Read reference string
        referenceString = getReferenceString(input);
        break;
      case 2:
        // Generate reference string
        referenceString = generateReferenceString(input);
        break;
      case 3:
        // Display current reference string
        if (referenceString.isEmpty()) {
          System.out.println("No reference string set");
          break;
        }
        System.out.println(referenceString);
        break;
      case 4:
        // Simulate FIFO
        simulateFifo(input, referenceString);
        break;
      case 5:
        // Simulate OPT
        simulateOpt(input, referenceString);
        break;
      case 6:
        // Simulate LRU
        simulateLru(input, referenceString);
        break;
      case 7:
        // Simulate LFU
        simulateLfu(input, referenceString);
        break;
      default:
        helpMenu();
    }
  }

  private static void helpMenu() {
    System.out.println("***FINAL PROJECT***");
    System.out.println("0: Exit");
    System.out.println("1: Read reference string");
    System.out.println("2: Generate reference string");
    System.out.println("3: Display current reference string");
    System.out.println("4: Simulate FIFO");
    System.out.println("5: Simulate OPT");
    System.out.println("6: Simulate LRU");
    System.out.println("7: Simulate LFU");
    System.out.println("8: Print this help menu");
  }

  private static ArrayList<Integer> getReferenceString(Scanner input) {
    System.out.println(
        "Enter a reference string [" +
            MIN_VIRTUAL_FRAME +
            "-" +
            MAX_VIRTUAL_FRAME +
            "]"
    );
    String[] string = input.nextLine().split("\\s+");
    ArrayList<Integer> refString = new ArrayList<>();
    int temp;

    for (int i = 0; i < string.length; i++) {
      try {
        temp = Integer.parseInt(string[i]);
      } catch (NumberFormatException e) {
        System.out.println("Reference string must only contain numbers");
        System.out.println(string[i] + " is not a number");
        return null;
      }
      if (temp > MAX_VIRTUAL_FRAME || temp < MIN_VIRTUAL_FRAME) {
        System.out.println(string[i] + " is not within the required range");
        return getReferenceString(input);
      }
      refString.add(i, temp);
    }
    return refString;
  }

  private static ArrayList<Integer> generateReferenceString(Scanner input) {
    System.out.println("Enter the desired string length: ");
    String lengthString = input.nextLine();
    int length = Integer.parseInt(lengthString);
    ArrayList<Integer> referenceString = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < length; i++) {
      referenceString
          .add(i, random.nextInt((MAX_VIRTUAL_FRAME - MIN_VIRTUAL_FRAME) + 1) + MIN_VIRTUAL_FRAME);
    }
    System.out.println("Reference String generated");
    System.out.println(referenceString);
    return referenceString;
  }

  private static String[][] generateTable(ArrayList<Integer> referenceString, int frames) {
    String[][] table = new String[frames + 3][referenceString.size() + 1];
    table[0][0] = "Reference String";
    for (int i = 1; i < frames; i++) {
      table[1][0] = "Physical Frame " + (i - 1);
    }
    table[frames + 1][0] = "Page Faults";
    table[frames + 2][0] = "Victim Frames";

    for (int i = 0; i < referenceString.size(); i++) {
      table[0][i + 1] = String.valueOf(referenceString.get(i));
    }

    return table;
  }

  private static void printTable(String[][] table) {
    for (String[] row : table) {
      for (int col = 0; col < row.length; col++) {
        if (col == 0) {
          System.out.printf("%-18s", row[0]);
        } else {
          if (row[col] == null) {
            System.out.printf("%4s", " ");
          } else {
            System.out.printf("%4s", row[col]);
          }
        }
      }
      System.out.println();
    }
  }

  private static void simulateFifo(Scanner input, ArrayList<Integer> referenceString) {
    System.out.println("Simulating FIFO");
    System.out.println(
        "Enter the number of physical frames [" +
            MIN_PHYSICAL_FRAME +
            "-" +
            MAX_PHYSICAL_FRAME +
            "]"
    );
    int frames = Integer.parseInt(input.nextLine());

    ArrayList<Integer> memory = new ArrayList<>(frames);
    String[][] table = generateTable(referenceString, frames);

    int victim = -1;
    boolean fault;
    int currentFrame = 0;
    int faultCount = 0;

    System.out.println("Starting FIFO");
    printTable(table);
    System.out.println("\nPress Enter to continue");
    try {
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < referenceString.size(); i++) {
      if (!memory.contains(referenceString.get(i))) {
        if (memory.size() < frames) {
          memory.add(currentFrame, referenceString.get(i));
          ++currentFrame;
          fault = true;
          faultCount++;
        } else {
          if (currentFrame >= frames) {
            currentFrame = 0;
          }

          fault = true;
          faultCount++;
          victim = memory.get(currentFrame);
          memory.set(currentFrame, referenceString.get(i));
          ++currentFrame;
        }
      } else {
        fault = false;
      }

      for (int j = 0; j < memory.size(); ++j) {
        table[j + 1][i + 1] = String.valueOf(memory.get(j));
      }

      if (fault) {
        table[frames + 1][i + 1] = "F";
        if (victim != -1) {
          table[frames + 2][i + 1] = String.valueOf(victim);
        }
      }

      System.out.println("Current Table\n");
      printTable(table);

      System.out.println("\nPress enter to continue...");
      try {
        System.in.read();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println("\nFIFO is now complete");
    System.out.println("A total of " + faultCount + " faults occurred");
  }

  private static void simulateOpt(Scanner input, ArrayList<Integer> referenceString) {
    System.out.println("Simulating OPT");
    System.out.println(
        "Enter the number of physical frames [" +
            MIN_PHYSICAL_FRAME +
            "-" +
            MAX_PHYSICAL_FRAME +
            "]"
    );
    int frames = Integer.parseInt(input.nextLine());
    ArrayList<Integer> memory = new ArrayList<>(frames);
    ArrayList<Integer> refList = new ArrayList<>();

    for (int i : referenceString)  // Create a reference list to
      refList.add(i);        // to search future

    String[][] table = generateTable(referenceString, frames);

    int victim = -1;
    boolean fault;
    int currentFrame = 0;
    int faultCount = 0;
    int max = -1;
    int index;

    System.out.println("Beginning OPT Simulation");

    System.out.println("Current Table\n");
    printTable(table);

    System.out.print("\nPress Enter to continue... ");
    try {
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < referenceString.size(); i++) {
      if (!memory.contains(referenceString.get(i))) {
        if (memory.size() < frames) {
          memory.add(currentFrame, referenceString.get(i));
          refList.remove((Integer) referenceString.get(i));  // Remove first occurrence of ref from list don't care about past
          ++currentFrame;
          fault = true;
          faultCount++;
        } else { // Page fault on full memory, swap
          fault = true;
          faultCount++;
          // Step 1 remove current item.
          int temp = refList.get(0);
          refList.remove(0);

          // Step 2 find ref that will not be used for longest period
          for (int m : memory) {
            index = refList.indexOf(m);

            // Simplest case, ref is never seen again
            if (index == -1) {
              victim = m;
              break;
            }

            if (index > max) { // Find max index into reference string
              victim = m;      // that will be the victim unless an index
              max = index;     // of -1 comes by.
            }
          }

          memory.set(memory.indexOf(victim), temp); // Swap
          max = -1;  // Reset max
        }
      } else { // Memory contains reference
        fault = false;
        refList.remove(0);
      }

      for (int j = 0; j < memory.size(); ++j) {
        table[j + 1][i + 1] = String.valueOf(memory.get(j));
      }

      if (fault) {
        table[frames + 1][i + 1] = "F";
        if (victim != -1)
          table[frames + 2][i + 1] = String.valueOf(victim);
      }

      System.out.println("Current Table\n");
      printTable(table);

      System.out.print("\nPress Enter to continue... ");
      try {
        System.in.read();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println("\nOPT  is now complete");
    System.out.println("A total of " + faultCount + " faults occurred");
  }

  private static void simulateLru(Scanner input, ArrayList<Integer> referenceString) {
    System.out.println("Simulating LRU");
    System.out.println(
        "Enter the number of physical frames [" +
            MIN_PHYSICAL_FRAME +
            "-" +
            MAX_PHYSICAL_FRAME +
            "]"
    );
    int frames = Integer.parseInt(input.nextLine());

    ArrayList<Integer> memory = new ArrayList<>(frames);
    int[] lruCount = new int[frames];

    String[][] table = generateTable(referenceString, frames);

    int victim = -1;
    boolean fault;
    int currentFrame = 0;
    int faultCount = 0;
    int max;
    int index;

    System.out.println("Starting LRU");
    printTable(table);

    System.out.print("\nPress enter to continue... ");
    try {
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < referenceString.size(); ++i) {
      if (!memory.contains(referenceString.get(i))) {
        if (memory.size() < frames) {
          memory.add(currentFrame, referenceString.get(i));
          for (int j = 0; j < lruCount.length; ++j) {
            lruCount[j]++;
          }
          lruCount[currentFrame] = 1;
          ++currentFrame;
          fault = true;
          faultCount++;
        } else {
          max = -1;
          index = 0;
          fault = true;
          faultCount++;


          for (int j = 0; j < lruCount.length; ++j) {
            if (lruCount[j] > max) {
              max = lruCount[j];
              index = j;
            }
          }

          victim = memory.get(index);
          memory.set(index, referenceString.get(i));

          for (int j = 0; j < lruCount.length; ++j) {
            lruCount[j]++;
          }
          lruCount[memory.indexOf(referenceString.get(i))] = 1;

        }
      } else {
        fault = false;
        for (int j = 0; j < lruCount.length; ++j) {
          lruCount[j]++;
        }
        lruCount[memory
            .indexOf(referenceString.get(i))] = 1;
      }

      for (int j = 0; j < memory.size(); ++j) {
        table[j + 1][i + 1] = String.valueOf(memory.get(j));
      }

      if (fault) {
        table[frames + 1][i + 1] = "F";
        if (victim != -1) {
          table[frames + 2][i + 1] = String.valueOf(victim);
        }
      }

      System.out.println("Current Table\n");
      printTable(table);

      System.out.print("\nPress enter to continue... ");
      try {
        System.in.read();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println("\nLRU  is now complete");
    System.out.println("A total of " + faultCount + " occurred");
  }

  private static void simulateLfu(Scanner input, ArrayList<Integer> referenceString) {
    System.out.println("Simulating LFU");
    System.out.println(
        "Enter the number of physical frames [" +
            MIN_PHYSICAL_FRAME +
            "-" +
            MAX_PHYSICAL_FRAME +
            "]"
    );
    int frames = Integer.parseInt(input.nextLine());

    ArrayList<Integer> memory = new ArrayList<>(frames);
    HashMap<Integer, Integer> lfuCount = new HashMap<>();

    String[][] table = generateTable(referenceString, frames);

    int victim = -1;
    boolean fault;
    int currentFrame = 0;
    int faultCount = 0;
    int min;
    int index;
    int count;

    System.out.println("Starting LFU");
    printTable(table);

    System.out.println("\nPress enter to continue... ");
    try {
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int i = 0; i < referenceString.size(); ++i) {
      if (!memory.contains(referenceString.get(i))) {
        if (memory.size() < frames) {
          memory.add(currentFrame, referenceString.get(i));
          lfuCount.put(referenceString.get(i), 1);
          ++currentFrame;
          fault = true;
          faultCount++;
        } else {
          min = lfuCount.get(memory.get(0));
          index = 0;
          fault = true;
          faultCount++;

          for (int j = 0; j < memory.size(); ++j) {
            if (lfuCount.get(memory.get(j)) < min) {
              min = lfuCount.get(memory.get(j));
              index = j;
            }
          }

          victim = memory.get(index);
          memory.set(index, referenceString.get(i));

          if (lfuCount.containsKey(referenceString.get(i))) {
            count = lfuCount.get(referenceString.get(i));
            count++;
            lfuCount.put(referenceString.get(i), count);
          } else {
            lfuCount.put(referenceString.get(i), 1);
          }
        }
      } else {
        fault = false;
        count = lfuCount.get(referenceString.get(i));
        count++;
        lfuCount.put(referenceString.get(i), count);
      }

      for (int j = 0; j < memory.size(); ++j) {
        table[j + 1][i + 1] = String.valueOf(memory.get(j));
      }

      if (fault) {
        table[frames + 1][i + 1] = "F";
        if (victim != -1) {
          table[frames + 2][i + 1] = String.valueOf(victim);
        }
      }

      System.out.println("Current Table\n");
      printTable(table);

      System.out.print("\nPress enter to continue... ");
      try {
        System.in.read();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println("\nLFU  is now complete");
    System.out.println("A total of " + faultCount + " faults occurred");
  }

  public static void main(String[] args) throws IOException {
    Scanner optionSelection = new Scanner(System.in);

    while (true) {
      helpMenu();
      System.out.print("Select Option: ");
      processInput(Integer.parseInt(optionSelection.nextLine().trim()), optionSelection);
    }
  }
}
