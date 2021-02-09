//Pranav Gaddameedi
//Hangman


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Hangman{

    public static int wordSize = 0;
    public static int size = 0;
    public static int wrong = 0;
    public static int correctGuess = 0;
    public static Character finalGuess;
    public static boolean playAgain = true;

    public static Set<String> chooseSize(Set<String> words, int bigword){
        Set<String> wordsReturned = new HashSet<>();
        Scanner sc = new Scanner(System.in);
        System.out.println("How many letters do you want the word to be? : ");
        wordSize = sc.nextInt();
        while(wordSize <= 0 || wordSize >= bigword) {
            System.out.println("Try again, no words in dictionary are that size: ");
            wordSize = sc.nextInt();
        }
        if(wordSize > 0 && wordSize <= bigword) {
            size = 0;
            for(String word: words) {
                if(word.length() == wordSize) {
                    wordsReturned.add(word);
                    size++;
                }
            }
        }
        return wordsReturned;
    }

    public static Map<String, List<String>> groupWords(List<String> wordList, Set<Character> guessed){
        Map<String, List<String>> groups = new HashMap<>();
        for(String word: wordList) {
            String wordgroup = "";
            for(int i = 0; i < word.length(); i++) {
                if(guessed.contains(word.charAt(i))) {
                    wordgroup += word.charAt(i);
                }else {
                    wordgroup += "_";
                }
            }
            if(groups.containsKey(wordgroup)) {
                List<String> list = groups.get(wordgroup);
                list.add(word);
                groups.put(wordgroup, list);
            }else {
                List<String> list = new ArrayList<>();
                list.add(word);
                groups.put(wordgroup, list);
            }
        }
        return groups;
    }

    public static String closestWords(Map<String, List<String>> groups) {
        int s = 0;
        String toReturn = "";
        for(Map.Entry<String, List<String>> entry : groups.entrySet()) {
            if(entry.getValue().size() > s) {
                s = entry.getValue().size();
                toReturn = entry.getKey();
            }
        }
        return toReturn;
    }

    public static String reveal(List<String> list, Character h) {
        for(String word: list) {
            int count = 0;
            for(int i = 0; i < word.length(); i++) {
                if(word.charAt(i) != h) {
                    count++;
                }
            }
            if(count == word.length()) {
                return word;
            }
        }
        return list.get(0);
    }

    public static boolean toWin(List<String> list, String word) {
        if(list.size() == 1 && list.get(0).contentEquals(word)) {
            return true;
        }else {
            return false;
        }
    }

    public static void play(Set<String> words, int guess) {
        List<String> wordlist = new ArrayList<>();
        Map<String, List<String>> wordgroups = new HashMap<>();
        for(String word: words) {
            wordlist.add(word);
        }
        Scanner sc = new Scanner(System.in);
        Set<Character> used = new HashSet<>();
        String word = "";
        System.out.println();
        System.out.println();
        for(int i = 0; i < size; i++) {
            word += " ";
        }
        while(wrong > 0 && !(toWin(wordlist, word))) {
            System.out.println("Number of guess: " + wrong);
            System.out.println(word);
            System.out.println();
            System.out.println("Guessed letters: " + used);
            System.out.println("Guess a letter: ");
            String s = sc.next();
            Character c = s.charAt(0);
            while(used.contains(c)) {
                System.out.println("You have already guessed that letter, guess again: ");
                String h = sc.next();
                c = h.charAt(0);
            }
            used.add(c);
            wordgroups = groupWords(wordlist, used);
            String groupchoice = closestWords(wordgroups);
            wordlist = wordgroups.get(groupchoice);
            System.out.println(wordlist);
            if(word.contentEquals(groupchoice)) {
                wrong--;
                if(wrong == 0) {
                    finalGuess = c;
                }
            }else {
                correctGuess++;
            }
            word = groupchoice;
        }
        if((toWin(wordlist, word))) {
            System.out.println("You won");
        }else {
            System.out.println("Sorry, you lost, the word was: " +reveal(wordlist, finalGuess));
        }
        System.out.println("Would you like to play again?(y/n)");
        Scanner scan = new Scanner(System.in);
        String ans = scan.next();
        if(ans.equalsIgnoreCase("y")) {
            playAgain = true;
        }else {
            playAgain = false;
        }
    }

    public static void main(String[] args) {
        Set<String> words = new HashSet<>();
        String fileName = "words.txt";
        int largestWord = 0;
        while(Hangman.playAgain) {
            try {
                Scanner scanner = new Scanner(new File(fileName));
                while(scanner.hasNext()) {
                    String g = scanner.next();
                    int h = g.length();
                    if(h > largestWord) {
                        largestWord = h;
                    }
                    words.add(g);
                    size++;
                }
            }catch(FileNotFoundException e1) {
                e1.printStackTrace();
            }
            System.out.println("Let's Play Hangman!");
            words = chooseSize(words, largestWord);
            Scanner g = new Scanner(System.in);
            System.out.println("How many guesses would you like? : ");
            wrong = g.nextInt();
            play(words, wrong);
        }
    }



}