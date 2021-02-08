import circularlinkedlist.CircularLinkedList;

import java.util.*;

public class Encryption {

    public static void main(String[] args){
        // Takes in the messages to be encrypted
        List<String> messages = new ArrayList<String>();
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the phrase you want to encrypt. Press enter to enter next phrase. Type '1' when complete.");
        String word = s.nextLine();
        while (!word.equals("1")){
            messages.add(word.toLowerCase());
            System.out.println("Enter the phrase you want to encrypt. Press enter to enter next phrase. Type '1' when complete.");
            word = s.nextLine();
        }
        //Creates an arraylist of arraylists that have the spaces and size of each message
        List<List<Integer>> indicies = getSpaces(messages);
        //Adds all messages together into one string and removes spaces
        String text = "";
        for (int i = 0; i < messages.size(); i++){
            text += messages.get(i).toLowerCase();
        }
        text = text.replaceAll("\\s", "");
        // getKeystream gets an array of key values from solitaire encryption algorithm
        int[] array = getKeystream(text.length());
        // encrypts original messages with key values
        String cipher = "";
        for (int i = 0; i < text.length(); i++) {
            cipher += encryptChar(text.charAt(i), array[i]);
        }
        // adds spaces back into encrypted string and splits it back up into original message lengths
        List<String> cryptWords = addSpaces(indicies, cipher, messages);
        System.out.println("Your encrypted messages are:");
        for (int i = 0; i < cryptWords.size(); i++) {
            System.out.println(cryptWords.get(i));
        }
        // decrypts encrypted messages if asked for
        String hey = "";
        for (int i = 0; i < text.length(); i++) {
            hey += decryptChar(cipher.charAt(i), array[i]);
        }
        // adds spaces back to decrypted(original) messages
        List<String> origDecrypt = addSpaces(indicies, hey, messages);
        // prints decrypted(original) messages if asked for
        System.out.println("Press 1 to get decrypt encryption, or anything else to exit.");
        String word2 = s.next();
        if(word2.equals("1")){
            System.out.println("Your decrypted messages are:");
            for (int i = 0; i < cryptWords.size(); i++) {
                System.out.println(origDecrypt.get(i));
            }
        }
        s.close();
    }
    //adds a character at a certain position in a string
    public static String addChar(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }

    //adds spaces back to string that was encrypted, and splits it into elements of the arraylist based on original messages
    public static List<String> addSpaces(List<List<Integer>> indicies, String cipher, List<String> messages){
        List<String> wee = new ArrayList<>();
        for (int i = 0; i < indicies.size(); i++) {
            for (int j = 0; j < indicies.get(i).size(); j++) {
                if (indicies.get(i).get(j) == messages.get(i).length()) {
                    wee.add(cipher.substring(0, indicies.get(i).get(j)));
                    cipher = cipher.substring(indicies.get(i).get(j));
                }else{
                    cipher = addChar(cipher, ' ', indicies.get(i).get(j));
                }
            }
        }
        return wee;
    }

    // Gets indicies of where the spaces are in each message inputted as an arraylist of arraylists
    public static List<List<Integer>> getSpaces(List<String> messages){
        List<List<Integer>> indicies = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            List<Integer> l = new ArrayList<>();
            int index = messages.get(i).indexOf(' ');
            while(index >= 0){
                l.add(index);
                index = messages.get(i).indexOf(' ', index + 1);
            }
            l.add(messages.get(i).length());
            indicies.add(l);
        }
        return indicies;
    }

    // loop that finds and returns the key values for each letter of the encrypted string
    public static int[] getKeystream(int length) {
        CircularLinkedList<Integer> list = new CircularLinkedList<Integer>();
        int[] array = new int[]{1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 3, 6, 9, 12, 15, 18, 21, 24, 27, 2, 5, 8, 11, 14, 17, 20, 23, 26};
        // converts array to CircularLinkedList
        readDeck(array, list);
        //array we will add values to and eventually return
        int[] retval = new int[length];
        for (int i = 0; i < length; i++) {
            //runs solitaire algorithm for one letter, replaces the list with the new order of the list
            list = runAlgo(list);
            //Step 5: takes card at the top of the deck and adds that value to return array
            int val = list.getItemAt(list.getItemAt(0));
            //if it is a joker, ignore and repeat the process, i-- to basically erase that turn
            if (val != 27 && val != 28){
                retval[i] = val;
            } else {
                i--;
            }
        }
        return retval;
    }

    public static CircularLinkedList<Integer> runAlgo(CircularLinkedList<Integer> list){
        //Step 1: swaps first joker with card above it - mod 28 in case it is at the end of the deck
        int jokerA = list.findCard(27);
        swap(list, jokerA, (jokerA + 1) % 28);
        //Step 2: swaps second joker and move two cards down
        int jokerB = list.findCard(28);
        swap(list, jokerB, (jokerB + 1) % 28);
        jokerB = list.findCard(28);
        swap(list, jokerB, (jokerB + 1) % 28);
        //Step 3: triplecut, reorients the order of the list
        list = tripleCut(list);
        //Step 4: moves the first x cards of the deck in front of the bottom card on the bottom
        // of the deck based on the value of the bottom card, and returns the new order of the list.
        return moveToBottom(list, list.getItemAt(list.getSize() - 1));
    }

    public static CircularLinkedList<Integer> moveToBottom(CircularLinkedList<Integer> list, int bottomCard){
        //adds cards to bottom of the deck until it hits the value of the bottom card
        int i = 0;
        while(i < bottomCard){
            list.add(list.remove(0));
            i++;
        }
        //finds the card that was on the bottom of the deck and moves it back to the bottom of the deck
        if (bottomCard != 28){
            list.add(list.remove(list.findCard(bottomCard)));
        }
        return list;
    }

    public static CircularLinkedList<Integer> tripleCut(CircularLinkedList<Integer> list){
        //Creates three temp CircularLinkedLists to move parts of the deck to
        CircularLinkedList<Integer> top = new CircularLinkedList<>();
        CircularLinkedList<Integer> middle = new CircularLinkedList<>();
        CircularLinkedList<Integer> bottom = new CircularLinkedList<>();

        //removes cards from original deck to top until we get to a joker
        while(list.getItemAt(0) != 27 && list.getItemAt(0) != 28){
            top.add(list.remove(0));
        }
        //the top card of the deck should be a joker, so we add that to the middle portion of the deck
        middle.add(list.remove(0));
        //loops again and adds card to middle until it reaches the second joker
        while(list.getItemAt(0) != 27 && list.getItemAt(0) != 28){
            middle.add(list.remove(0));
        }
        //adds second joker to the deck
        middle.add(list.remove(0));
        //adds the rest of the cards to the bottom part of the deck, the new "top" of the deck
        while(list.getSize() != 0){
            bottom.add(list.remove(0));
        }
        //adds the middle cards beneath the old bottom part of the deck
        while(middle.getSize() != 0){
            bottom.add(middle.remove(0));
        }
        //adds the old top cards to the bottom of the new deck we will have
        while(top.getSize() != 0){
            bottom.add(top.remove(0));
        }
        //returns the new deck configuration
        return bottom;
    }

    //converts array of cards to CircularLinkedList
    public static void readDeck(int[] array, CircularLinkedList<Integer> list){
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
    }
    //swaps two cards at two different positions in CircularLinkedList
    public static void swap(CircularLinkedList<Integer> list, int pos1, int pos2){
        Integer a = list.getItemAt(pos1);
        Integer b = list.getItemAt(pos2);
        list.setItemAt(pos1, b);
        list.setItemAt(pos2, a);
    }


    public static char encryptChar(char c, int key){
        char plaintext = (char)(c -'a');
        char ciphertext = (char)((plaintext + key) % 26);
        return (char)(ciphertext + 'a');
    }

    public static char decryptChar(char c, int key){
        char plaintext = (char)(c -'a');
        char ciphertext = (char)((plaintext + (26 - key)) % 26);
        return (char)(ciphertext + 'a');

    }
}