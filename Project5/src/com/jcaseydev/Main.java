package com.jcaseydev;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

  private static Path directoryPath = null;
  private static File file = null;

  private static void processInput(int option, Scanner input) throws IOException {
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
        // display offset of hex
        displayFileHex(input);
        break;
      case 6:
        // encrypt file XOR
        fileBytes(input, 0);
        break;
      case 7:
        // decrypt file XOR
        fileBytes(input, 1);
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
    System.out.print("Enter a path: ");
    String inputPath = input.nextLine();
    while (!Files.exists(Paths.get(inputPath))) {
      System.out.println(inputPath + " does not exist");
      System.out.print("Enter a correct path: ");
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

  private static void listDirectoryRecursive() throws IOException {
    Files.walk(Paths.get(String.valueOf(directoryPath)))
            .filter(Files::isRegularFile)
            .forEach(System.out::println);
  }

  private static void deleteFile(Scanner input) {
    if (directoryPath == null) {
      System.out.println("Enter Directory using Option 1");
      return;
    }

    System.out.print("Enter filename: ");
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

    System.out.print("Enter filename: ");
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
    }
    ;
  }

  private static void fileBytes(Scanner input, int cryptOption) {
    if (directoryPath == null) {
      System.out.println("Enter Directory using Option 1");
      return;
    }

    // get file to encrypt
    System.out.print("Enter file name: ");
    String fileName = input.nextLine();
    String filePath = directoryPath + "/" + fileName;


    System.out.print("Enter password: ");
    String password = input.nextLine();
    if (password.getBytes().length > 256) {
      System.out.println("Password too long. (must be < 256)");
    }

    try {
      byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

      byte[] passwordBytes = password.getBytes();

      if (cryptOption == 0) {
        encryptXor(fileBytes, passwordBytes, fileName);
      } else {
        decryptXor(fileBytes, passwordBytes, fileName);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void encryptXor(byte[] fileBytes, byte[] passwordBytes, String fileName) {
    try {
      int j = 0;

      for (int i = 0; i < fileBytes.length; i++) {

        if (j > passwordBytes.length - 1) {
          j = 0;
        }
        fileBytes[i] = (byte) (fileBytes[i] ^ passwordBytes[j]);

        j++;
      }
      //Creates new file
      File outputFile = new File(directoryPath + "/" + "ENC-" + fileName);
      FileOutputStream stream = new FileOutputStream(outputFile);

      try {
        stream.write(fileBytes);
      } finally {
        stream.close();
      }
    } catch (IOException e) {
      System.out.println("Your file was not found... Returning to menu.");
      return;
    }
  }

  private static void decryptXor(byte[] fileBytes, byte[] passwordBytes, String fileName) {
    try {
      int j = 0;
      for (int i = 0; i < fileBytes.length; i++) {

        if (j > passwordBytes.length - 1) {
          j = 0;
        }

        fileBytes[i] = (byte) (passwordBytes[j] ^ fileBytes[i]);
        j++;
      }
      File outputFile = new File(directoryPath + "/" +"DEC-" + fileName);
      FileOutputStream stream = new FileOutputStream(outputFile);

      try {
        stream.write(fileBytes);
      } finally {
        stream.close();
      }
    } catch (IOException e) {
      System.out.println("Your file was not found... Returning to menu.");
    }
  }

  public static void main(String[] args) throws IOException {
    // write your code here
    Scanner optionSelection = new Scanner(System.in);

    while (true) {
      helpMenu();
      System.out.print("Select Option: ");
      processInput(Integer.parseInt(optionSelection.nextLine().trim()), optionSelection);
    }
  }
}
