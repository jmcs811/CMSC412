package com.jcaseydev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

  private static Path directoryPath = null;

  private static void processInput(int option, Scanner input) {
    switch (option) {
      case 0:
        System.exit(1);
        break;
      case 1:
        //Select Directory
        selectDirectory(input);
        break;
      case 2:
        // TODO: list directory content (first level)
        try {
          listDirectory();
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      case 3:
        // TODO: list directory content all levels
        listDirectoryRecursive();
        break;
      case 4:
        // Delete file
        deleteFile(input);
        break;
      case 5:
        // TODO: display offset of hex
        displayFileHex(input);
        break;
      case 6:
        // TODO: encrypt file XOR
        encryptXor(input);
        break;
      case 7:
        // TODO: decrypt file XOR
        decryptXor(input);
        break;
      default:
        helpMenu();
    }
  }

  private static void helpMenu() {
    System.out.println("***PROJECT 5***");
    System.out.println("0: Exit");
    System.out.println("1: Select Directory");
    System.out.println("2: List Directory (first lvl)");
    System.out.println("3: List Directory (all lvls)");
    System.out.println("4: Delete File");
    System.out.println("5: Display File (hex view)");
    System.out.println("6: Encrypt File (XOR with pwd)");
    System.out.println("7: Decrypt File (XOR with pwd)");
    System.out.println("8: Print this help menu");
  }

  private static void selectDirectory(Scanner input) {
    System.out.println("Enter a path:");
    String inputPath = input.nextLine();
    while (!Files.exists(Paths.get(inputPath))) {
      System.out.println(inputPath + " does not exist");
      System.out.println("Enter a correct path: ");
      inputPath = input.nextLine();
    }
    directoryPath = Paths.get(inputPath);
    System.out.println("\nDirectory Set to " + directoryPath.toString());
  }

  private static void listDirectory() throws IOException {
    if (!(directoryPath == null)) {
      Files.list(new File(directoryPath.toString()).toPath())
          .forEach(System.out::println);
    } else {
      System.out.println("Enter Directory using Option 1");
    }
  }

  private static void listDirectoryRecursive() {}

  private static void deleteFile(Scanner input) {
    if (directoryPath == null) {
      System.out.println("Enter Directory using Option 1");
      return;
    }
    String filePath = directoryPath + "/" + input.nextLine();
    try {
      Files.delete(Paths.get(filePath));
      System.out.println(filePath + " has been deleted");
    } catch (NoSuchFileException x) {
      System.out.println("No such File");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void displayFileHex(Scanner input) {
    if (directoryPath == null) {
      System.out.println("Enter Directory using Option 1");
      return;
    }
    String filePath = directoryPath + "/" + input.nextLine();
    try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
      int i = 0;
      int count = 0;

      while ((i = fileInputStream.read()) != -1) {
        System.out.printf("%02x ", i);
        count++;

        if (count == 16) {
          System.out.println("");
          count = 0;
        }
      }
      System.out.println("");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } ;
  }

  private static void encryptXor(Scanner input) {}

  private static void decryptXor(Scanner input) {

  }

    public static void main(String[] args) {
	    // write your code here
      Scanner optionSelection = new Scanner(System.in);

      while (true) {
        helpMenu();
        System.out.println("Select Option");
        processInput(Integer.parseInt(optionSelection.nextLine()), optionSelection);
      }
    }
}
