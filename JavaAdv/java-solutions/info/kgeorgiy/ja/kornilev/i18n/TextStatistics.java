
package info.kgeorgiy.ja.kornilev.i18n;
import info.kgeorgiy.ja.kornilev.i18n.MyPair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.*;

public class TextStatistics {
    String fileName = "";
    int numOfSentence = 0;
    int numOfDiffrentSentence;
    int numOfWords = 0;
    int getNumOfDiffrentWords;
    int numOfNumbers = 0;
    int numOfDiffrentNumbers;
    int numOfCurrencies = 0;
    int numOfDiffrentCurrencies = 0;
    int numOfDiffrentDate = 0;
    int numOfDates = 0;
    ArrayList<String> sentences;
    ArrayList<String> words;
    String minWord;
    String maxWord;
    String minLenWord = "";
    String maxLenWord = "";
    double midLenWord = 0;
    double midLenSentence = 0;
    ArrayList<Number> numbers;
    ArrayList<MyPair<Number, Character>> currencies;
    ArrayList<Date> dates;
    Number minNumber = Long.MAX_VALUE;
    Number maxNumber = 0;
    Number midNumber = 0;
    String minSentence = "";
    String maxSentence = "";
    String minLenSentence = "";
    String maxLenSentence = "";
    Date minDate;
    Date maxDate;
    Date midDate;
    String file;
    Locale  locale = Locale.getDefault();
    Locale outputLocale = Locale.getDefault();
    String output = "";
    MyPair<Number, Character> minCurriency;
    MyPair<Number, Character> maxCurriency;
    MyPair<Number, Character> midCurriency;
    public TextStatistics(String file, String fileName, Locale inputLocale, Locale outputLocale) {
        this.file = file;
        this.fileName = fileName;
        locale = inputLocale;
        this.outputLocale = outputLocale;
        for(int i = 0; i < 10000; i++){
            minLenSentence += " ";
            minLenWord += " ";
            minSentence += (char)255;
        }
    }

    private void countWords(){
        words = new ArrayList<>();
        numOfWords = 0;
        BreakIterator wordIterator = BreakIterator.getWordInstance(locale);
        wordIterator.setText(file);
        int start = wordIterator.first();
        int end = wordIterator.next();
        String curWord = "";
        while (end != BreakIterator.DONE){
            curWord = file.substring(start, end).trim();
            if(!curWord.isEmpty()){
                numOfWords++;
                words.add(curWord);
            }
            start = end;
            end = wordIterator.next();
        }
        if(!curWord.isEmpty()){
            numOfWords++;
            words.add(curWord);
        }
    }
    private void countSentence(){
        sentences = new ArrayList<>();
        numOfSentence = 0;
        BreakIterator wordIterator = BreakIterator.getSentenceInstance(locale);
        wordIterator.setText(file);
        int start = wordIterator.first();
        int end = wordIterator.next();
        String curSentence = "";
        while (end != BreakIterator.DONE){
            curSentence = file.substring(start, end).trim();
            if(!curSentence.isBlank()){
                sentences.add(curSentence);
            }
            start = end;
            end = wordIterator.next();
        }
        numOfSentence = sentences.size();
    }
    private void countNumbers(){
        countWords();
        numbers = new ArrayList<>();
        numOfNumbers = 0;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        for(String word : words){
            try {
                numbers.add(numberFormat.parse(word));
                numOfNumbers++;
            }catch (ParseException ignored){
            }
        }
        numOfNumbers = numbers.size();
    }
    public void countDates(){
        countWords();
        dates = new ArrayList<>();
        numOfDates = 0;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        for(String word : words){
            try {
                dates.add(dateFormat.parse(word));
                numOfDates++;
            }catch (ParseException ignored){
            }
        }
    }
    public void countCurrencies(){
        countWords();
        currencies = new ArrayList<>();
        numOfCurrencies = 0;
        NumberFormat numberFormat =NumberFormat.getNumberInstance(locale);
        String currSymbols = Currency.getAvailableCurrencies().stream().map(Currency::getSymbol).reduce("", (s1, s2) -> s1 + s2);
        for(String word: words){
            try {
                if(currSymbols.contains(Character.toString(word.charAt(0)))){
                    currencies.add(new MyPair<>(numberFormat.parse(word.substring(1)), word.charAt(0)));
                    numOfCurrencies++;
                }
                if(currSymbols.contains(Character.toString(word.charAt(word.length() - 1)))){
                    currencies.add(new MyPair<>(numberFormat.parse(word.substring(0, word.length() - 1)), word.charAt(word.length() - 1)));
                    numOfCurrencies++;
                }
            }catch (ParseException ignored){}

        }
    }
    public void summaryStatistics(){
        countWords();
        countSentence();
        countDates();
        countNumbers();
        countCurrencies();
    }
    public void sentenceStatistics(){
        countSentence();
        Set<String> acc = new TreeSet<>(sentences);
        numOfDiffrentSentence = acc.size();
        int idx = 0;
        int minSentenceLength = Integer.MAX_VALUE;
        double sum = 0;
        for(String sentence : acc){
            if(idx == 0){
                minSentence = sentence;
            }
            if(idx == acc.size()){
                maxSentence = sentence;
            }
            if(sentence.length() > maxLenSentence.length()){
                maxLenSentence = sentence;
            }
            if(sentence.length() < minSentenceLength){
                minSentence = sentence;
                minSentenceLength = sentence.length();
            }

            idx++;
        }
        for(String sentence : sentences){
            sum += sentence.length();
        }
        midLenSentence = sum / sentences.size();
    }
    public void wordStatistic(){
        countWords();
        Set<String> acc = new TreeSet<>(words);
        getNumOfDiffrentWords = acc.size();
        int idx = 0;
        int minWordLength = Integer.MAX_VALUE;
        for(String word : acc){
            if(idx == 0){
                minWord = word;
            }
            if(idx == acc.size()){
                maxWord = word;
            }
            if(word.length() > maxLenSentence.length()){
                maxLenSentence = word;
            }
            if(word.length() < minWordLength){
                minSentence = word;
                minWordLength = word.length();
            }
            idx++;
        }
        double sum = 0;
        for (String word : words){
            sum += word.length();
        }
        midLenWord = sum / words.size();
    }
    public void numberStatistic(){
        countNumbers();
        Set<Number> numbers1 = new TreeSet<>(numbers);
        numOfDiffrentNumbers = numbers1.size();
        int idx = 0;
        for(Number number : numbers1){
            if(idx == 0){
                minNumber = number;
            }
            if(idx == numbers1.size()){
                maxNumber = number;
            }
            idx++;
        }
        double sum = 0.0;
        for(Number number : numbers){
            sum += number.doubleValue();
        }
        midNumber = sum / numbers.size();
    }
    public void dateStatistic(){
        countDates();
        Set<Date> dates1 = new TreeSet<>(dates);
        numOfDiffrentNumbers = dates1.size();
        int idx = 0;
        for(Date date : dates1){
            if(idx == 0){
                minDate = date;
            }

            if(idx == dates1.size()){
                maxDate = date;
            }
            idx++;
        }
        double sum = 0.0;
        for(Date date : dates1){
            sum += date.getTime();
        }
        midDate = new Date((long) (sum / dates.size()));
    }
    private String getCorrectOutputWord(String word){
        ResourceBundle bundle = ResourceBundle.getBundle("info/kgeorgiy/ja/kornilev/i18n/resources/WordTranslation", outputLocale);
        return bundle.getString(word);
    }
    public String getStatistic(){
        numberStatistic();
        wordStatistic();
        sentenceStatistics();
        dateStatistic();
        curriencyStatistic();
        String output = "";
        output += getCorrectOutputWord("inputPhrase") + fileName + '\n';
        output += getCorrectOutputWord("allStatistic") + '\n';
        output += '\t' + getCorrectOutputWord("numberSentence") + numOfSentence + '\n';
        output += '\t' + getCorrectOutputWord("numberWords") + numOfWords + '\n';
        output += '\t' + getCorrectOutputWord("numberNumbers") + numOfNumbers + '\n';
        output += '\t' + getCorrectOutputWord("numberSums") + numOfCurrencies + '\n';
        output += '\t' + getCorrectOutputWord("numberDates") + numOfDates + '\n';

        output += getCorrectOutputWord("sentenceStats") + '\n';
        output += '\t' + getCorrectOutputWord("maximalSentence") + numOfSentence + " (" + getCorrectOutputWord("diffrent")  + numOfDiffrentSentence + ")" + '\n';
        output += '\t' + getCorrectOutputWord("minimalSentence") + '\"' +  minSentence +  '\"' + '\n';
        output += '\t' + getCorrectOutputWord("maximalSentence") + '\"' +  maxSentence +  '\"' + '\n';
        output += '\t' + getCorrectOutputWord("minimalLenSentence") +  minLenSentence.length() + "(" + '\"' +  minLenSentence +  '\"' + ")" + '\n';
        output += '\t' + getCorrectOutputWord("maximalLengthSentence") +  maxLenSentence.length() + "(" + '\"' +  maxLenSentence +  '\"' + ")" + '\n';
        output += '\t' + getCorrectOutputWord("avaregeLengthSentence") + '\"' +  minSentence +  '\"' + '\n';

        output += getCorrectOutputWord("wordStat") + '\n';
        output += '\t' + getCorrectOutputWord("numberWords") + numOfSentence  + " (" + getCorrectOutputWord("diffrent") + getNumOfDiffrentWords  + ")" + '\n';
        output += '\t' + getCorrectOutputWord("minimalWord") + '\"' +  minWord +  '\"' + '\n';
        output += '\t' + getCorrectOutputWord("maximalWord") + '\"' +  maxWord +  '\"' + '\n';
        output += '\t' + getCorrectOutputWord("minimalLenWord") +  minLenWord.length() + "(" + '\"' +  minLenWord +  '\"' + ")" + '\n';
        output += '\t' + getCorrectOutputWord("maximalLenWord") +  maxLenWord.length() + "(" + '\"' +  maxLenWord +  '\"' + ")" + '\n';
        output += '\t' + getCorrectOutputWord("averageLenWord") + '\"' +  minWord +  '\"' + '\n';

        output += getCorrectOutputWord("numberStats") + '\n';
        output += '\t' + getCorrectOutputWord("numberNumbers") + numOfNumbers + '\n';
        output += '\t' + getCorrectOutputWord("minimalNumber") +  minNumber + '\n';
        output += '\t' + getCorrectOutputWord("maximalNumber") +  maxNumber + '\n';
        output += '\t' + getCorrectOutputWord("averageNumber") +  midNumber + '\n';

        output += getCorrectOutputWord("curriencyStat") + '\n';
        output += '\t' + getCorrectOutputWord("numberSums") + numOfCurrencies + ' ';
        output += '\t' + getCorrectOutputWord("minimalCurriency") +  minCurriency.getFirst() + ' ' + minCurriency.getSecond() + '\n';
        output += '\t' + getCorrectOutputWord("maximalCurriency") +  maxCurriency.getFirst() + ' ' +  minCurriency.getSecond() + '\n';
        output += '\t' + getCorrectOutputWord("averageCurriency")+  midCurriency.getFirst() + ' ' + minCurriency.getSecond() + '\n';

        output += getCorrectOutputWord("dateStat") + '\n';
        output += '\t' + getCorrectOutputWord("numberDates") + numOfDates + '\n';
        output += '\t' + getCorrectOutputWord("minDate") +  minDate + ' '  + '\n';
        output += '\t' + getCorrectOutputWord("maxDate") +  maxDate + ' ' + '\n';
        output += '\t' + getCorrectOutputWord("averageDate")+  midDate + ' ' + '\n';

        return output;
    }
    public void curriencyStatistic(){
        countCurrencies();
        double sum = 0;
        double minNum = Long.MAX_VALUE;
        double maxNum = 0;
        Character minCurriency = null;
        Character maxCurriency = null;
        for(MyPair<Number, Character> pair : currencies){
            if(pair.getFirst().doubleValue() > maxNum){
                maxNum = pair.getFirst().doubleValue();
                maxCurriency = pair.getSecond();
            }
            if(pair.getFirst().doubleValue() < minNum){
                minNum = pair.getFirst().doubleValue();
                minCurriency = pair.getSecond();
            }
            sum += pair.getFirst().doubleValue();
        }
        midCurriency = new MyPair<>(sum / currencies.size(), maxCurriency);
        this.maxCurriency = new MyPair<>(maxNum, maxCurriency);
        this.minCurriency = new MyPair<>(minNum, minCurriency);
    }
    public static void main(String[] args) {
        if(args.length == 0){
            throw new RuntimeException("Incorrect number of arguments");
        }
        Locale inputLocale = new Locale(args[0]);
        Locale outputLocale = new Locale(args[1]);
        File inputFile = new File(args[2]);
        System.out.println(inputFile.getAbsolutePath());
        File  outputFile = new File(args[3]);
        try (FileWriter fileWriter = new FileWriter(outputFile);  ){
            Scanner scanner = new Scanner(inputFile);
            String file = "";
            while (scanner.hasNextLine()){
                file += scanner.nextLine() + '\n';
            }
            TextStatistics textStatistics = new TextStatistics(file, args[2], inputLocale, outputLocale);
            String ans = textStatistics.getStatistic();
            fileWriter.write(ans);
        }catch (IOException e){
            System.out.println("Something wrong with file");
        }
    }
}

