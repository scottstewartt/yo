package a;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FindNames {

    /** 
     * Problem:
     * 
     *  Input file 1 contains names and other strings.
     *  Input file 2 contains a list of names, one name per line.
     * 
     * Take these 2 files as input and print to standard out the line numbers from file 1 where the
     * name in file 2 is found. For example, if file 2 contains the name Alice, expected output would be Alice: 4, 56, 200. 
     * This means in file 1, the name Alice appears in the file on line 4, 56, and 200.
     * 
     * Looking for the most efficient solution taking into account i/o, cpu, and memory usage.
     * You may state your assumptions and feel free to add additional justification as well.
     * 
     * 
     * Assumptions:
     *  * Names could contain spaces or other punctuation
     *  * List of names is relatively short and easily fits in memory
     *  * Matches are case-insensitive
     *  * Can match inside another word, so this would match 'Alice': "the chalice"
     *  
     *  adam
     *   lex
     *    ice
     *   ngela
     *  ben
     *    verely 
     **/
    public static void main(String[] args) throws Exception {
        BufferedReader namesIn = null;
        BufferedReader namesAndOtherStringsIn = null;
        try {
            namesIn = new BufferedReader(new FileReader("names.txt"));
            namesAndOtherStringsIn = new BufferedReader(new FileReader("namesAndOtherStrings.txt"));
            
            Node root = new Node((char)0);
            for (String name = namesIn.readLine(); name != null; name = namesIn.readLine())
                root.addName(name,  0);
            
            int lineNum = 1;
            for (String line = namesAndOtherStringsIn.readLine(); line != null; line = namesAndOtherStringsIn.readLine(), lineNum++)
                for (int i = 0; i < line.length(); i++) //Starting at each character count all names
                    root.countPrefixedNames(line, i, lineNum);
            
            root.printLines("");
            
        } finally {
            if (namesIn != null) namesIn.close();
            if (namesAndOtherStringsIn != null) namesAndOtherStringsIn.close();
        }
    }
    
    private static class Node {
        public final char c;
        public boolean isName = false;
        public Set<Integer> lineNums = null; //use set to avoid multiples 
        public List<Node> kids = null;
        
        public Node(char c) {
            this.c = Character.toLowerCase(c);
        }
        
        public void addLineNum(int lineNum) {
            if (lineNums == null) lineNums = new TreeSet<>(); //preserve order with tree set
            lineNums.add(lineNum);
        }
        
        public Node getOrAddKid(char kidChar) {
            Node kid = getKid(kidChar);
            if (kid == null)
                kid = addKid(kidChar);
            return kid;
        }
        
        public Node addKid(char c) {
            if (kids == null) kids = new ArrayList<>();
            Node kid = new Node(c);
            kids.add(kid);
            return kid;
        }
        
        public Node getKid(char kidChar) {
            if (kids == null) return null;
            kidChar = Character.toLowerCase(kidChar);
            for (Node kid: kids)
                if (kid.c == kidChar)
                    return kid;
            return null;
        }
        
        public void addName(String name, int index) {
            if (index == name.length()) {
                isName = true;
                return;
            }
            getOrAddKid(name.charAt(index)).addName(name, index +1);
        }
        
        // ann annie
        // annielski
        // Starting at index: iterate through string by tail-recursive calls, adding line num when finding a name
        // This was originally written to count lines efficiently but turns out works for iterating over the line characters
        // TODO: *Possible* additional efficiency: Since we only char if we've found the name once, find some way to trim it from 
        // the tree after we found it once; it may not be worth the extra computation or memory
        public void countPrefixedNames(String value, int index, int lineNum) {
            if (isName) 
                addLineNum(lineNum);
            if (index == value.length())
                return;
            Node kid = getKid(value.charAt(index));
            if (kid == null) return;
            kid.countPrefixedNames(value, index +1, lineNum);
        }
        
        public void printLines(String prefix) {
            prefix = prefix + c;
            if (isName)
                System.out.println(prefix +": "+ lineNums);
            if (kids == null) return;
            for (Node kid: kids)
                kid.printLines(prefix);
        }

        @Override
        public String toString() {
            return "Node [c=" + c + ", isName=" + isName + ", lineNums=" + lineNums + ", kids=" + kids + "]";
        }
    }
}


//public boolean contains(String value, int index) {
//  if (index == value.length())
//      return isName;
//  Node kid = getKid(value.charAt(index +1));
//  if (kid == null) return false;
//  return kid.contains(value, index +1);
//}
