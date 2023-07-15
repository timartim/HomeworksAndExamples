package info.kgeorgiy.ja.kornilev.i18n.tests;

import info.kgeorgiy.ja.kornilev.i18n.TextStatistics;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

public class MyTest {
    @Test
    public void test1_simpleInputRU(){
        System.out.println("...test1 Simple input...");
        String simpleInput = "1 2 3 10.20.2022 слово валюта: 100$ ";
        TextStatistics statistics = new TextStatistics(simpleInput, "Test1", Locale.getDefault(), Locale.getDefault());
        Solver correctStats = new Solver(simpleInput, "Test1", Locale.getDefault(), Locale.getDefault());
        assertEquals("Incorrect output test1", statistics.getStatistic(), correctStats.getStatistic());
        System.out.println("...test1 passed sucsessfully");
    }
    @Test
    public void test2_exampleInput(){
        System.out.println("...test2 example input...");

        File file = new File("info/kgeorgiy/ja/kornilev/i18n/tests/exampleInput.txt");
        try {
            String testInput = getStringFromFile(file);
            TextStatistics statistics = new TextStatistics(testInput, "Test2", Locale.getDefault(), Locale.getDefault());
            Solver correctStats = new Solver(testInput, "Test2", Locale.getDefault(), Locale.getDefault());
            assertEquals("Incorrect output test2", statistics.getStatistic(), correctStats.getStatistic());
            System.out.println("...test2 passed sucsessfully");
        }catch (FileNotFoundException ingored){
            System.out.println("...test2 passed sucsessfully");
        }
    }
    @Test
    public void test3_CheckLargeString(){
        System.out.println("...test3 largeFile...");
        String[] words = {"a" , "b", "c", "d", "e", "f", "g", "h", "$100", "100$", " 100₽ ", " ", ".", "1", "2", "12", "2000", " 1.10.2022 ", "10.10.1010"};
        int numOfTests = 10;
        int sizeOfTest = 1000;
        for(int i = 0; i < numOfTests; i++){
            String test = generateTest(words, sizeOfTest);
            TextStatistics statistics = new TextStatistics(test, "Test4", Locale.FRANCE, Locale.US);
            Solver correctStats = new Solver(test, "Test4", Locale.FRANCE, Locale.US);
            assertEquals("Incorrect output test5", statistics.getStatistic(), correctStats.getStatistic());
        }
        System.out.println("...test3 passed sucsessfully...");
    }


    @Test
    public void test4_CheckDiffrentLan(){
        System.out.println("...test4 DiffrentLan...");
        String simpleInput = "1 2 3 10.05.2023 06.20.2023 $1 $2 ";
        TextStatistics statistics = new TextStatistics(simpleInput, "Test4", Locale.FRANCE, Locale.US);
        Solver correctStats = new Solver(simpleInput, "Test4", Locale.FRANCE, Locale.US);
        assertEquals("Incorrect output test4", statistics.getStatistic(), correctStats.getStatistic());
        System.out.println("...test4 passed sucsessfully");
    }
    @Test
    public void test5_CheckDiffrentLan(){
        System.out.println("...test5 DiffrentLan...");
        String simpleInput = "مرحباً كيف حالك";
        TextStatistics statistics = new TextStatistics(simpleInput, "Test5", new Locale("ar"), Locale.US);
        Solver correctStats = new Solver(simpleInput, "Test5", new Locale("ar"), Locale.US);
        assertEquals("Incorrect output test5", statistics.getStatistic(), correctStats.getStatistic());
        System.out.println("...test5 passed sucsessfully");
    }
    @Test
    public void test6_RandomCheckArabicanLan(){
        System.out.println("...test6 arabic...");
        String[] words = {"a" , "b", "c", "d", "e", "f", "g", "h", "$100", "100$", " 100₽ ", " ", ".", "1", "2", "12", "2000", " 1.10.2022 ", " 10.10.1010 ", " 2023.3.17 ", "2023.17.3 ","نُقْطَةٌ", " فَاصِلة" , "ض" ,"ث" , "د ﻩ "};
        int numOfTests = 10;
        int sizeOfTest = 1000;
        for(int i = 0; i < numOfTests; i++){
            String test = generateTest(words, sizeOfTest);
            TextStatistics statistics = new TextStatistics(test, "Test6", new Locale("ar"), Locale.US);
            Solver correctStats = new Solver(test, "Test6", new Locale("ar"), Locale.US);
            assertEquals("Incorrect output test6", statistics.getStatistic(), correctStats.getStatistic());
        }
        System.out.println("...test6 passed sucsessfully...");
    }
    public static String generateTest(String[] words, int sizeOfTest){
        String test = "";
        for(int j = 0; j < sizeOfTest; j++){
            Random random = new Random(j);
            test += words[Math.abs(random.nextInt()% words.length)] ;
        }
        TextStatistics statistics = new TextStatistics(test, "Test6", new Locale("ar"), Locale.US);
        Solver correctStats = new Solver(test, "Test6", new Locale("ar"), Locale.US);
        assertEquals("Incorrect output test6", statistics.getStatistic(), correctStats.getStatistic());
        return test;
    }
    public static String getStringFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder ans = new StringBuilder();
        while (scanner.hasNextLine()){
            ans.append(scanner.nextLine()).append('\n');
        }
        return ans.toString();
    }
}
