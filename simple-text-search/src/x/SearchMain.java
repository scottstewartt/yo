package x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/*
 * A simple text search program that accepts a path to a directory and provides a prompt
 * where you can enter queries on the documents in that directory. 
 * Queries begin with 'AND' or 'OR' so:
 * entry query> AND cats dogs
 * entry query> OR birds zebras
 * And the output is the list of the paths to files containing either all the terms (AND) or any of the terms (OR)
 */
public class SearchMain {
    
    static void p(Object o) { System.out.println(o); }
    
    static void exitErr(Object o) {
        System.out.println(o);
        System.exit(1);
    }
    
    static void printMem() {
        Runtime r = Runtime.getRuntime();
        p("Total: "+ r.totalMemory() +", Free: "+ r.freeMemory() + ", Max: "+ r.maxMemory());
    }
    
    static void print1PerLine(Collection<?> coll) {
        for (Object o: coll)
            p(" * "+ o);
    }
    
    

    static HashMap<String, HashSet<String>> tokenToFilePaths = new HashMap<>();
    
    static void addFileToken(String token, String filePath) {
        HashSet<String> filesWithToken = tokenToFilePaths.get(token);
        if (filesWithToken == null) {
            filesWithToken = new LinkedHashSet<>(); // use Linked to make order deterministic for easier testing
            tokenToFilePaths.put(token, filesWithToken);
        }
        filesWithToken.add(filePath);
    }
    
    static void addFileTokens(String[] tokens, String filePath) {
        for (String token: tokens)
            addFileToken(token, filePath);
    }
    
    static Pattern whiteSpacePattern = Pattern.compile("\\s");
    
    static void indexFile(String filePath) throws Exception { indexFile(new File(filePath)); }
    static void indexFile(File file) throws Exception {
        if (file.isDirectory()) {
            for (File fileInDir: file.listFiles())
                indexFile(fileInDir);
            return;
        }
        // else: is regular file
        BufferedReader in = null;
        try {
            String filePath = file.getAbsolutePath().intern();
//            p("indexing "+ filePath +", keys.size="+ tokenToFilePaths.keySet().size() +", size="+ tokenToFilePaths.size());
//            printMem();
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) //TODO: collect all tokens first & remove duplicates?
                addFileTokens(whiteSpacePattern.split(line.toLowerCase()), filePath);
        } finally { 
            if (in != null) in.close();
        }
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            exitErr("Only accepts 1 argument: base file directory path");
        
        indexFile(args[0]);
        
        
        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            String inputLine = null;
            do {
                System.out.print("enter query> ");
                if (scanner.hasNext()) {
                    inputLine = scanner.nextLine();
                    String[] inputTokens = whiteSpacePattern.split(inputLine.toLowerCase());
                    if (inputTokens.length < 1)
                        continue;
                    String first = inputTokens[0];
                    switch (first) {
                    case "and":
                        printPathsWithAllTerms(inputTokens);
                        break;
                    case "or":
                        printPathsWithAnyTerm(inputTokens);
                        break;
                    case "exit":
                        break;
                    default : p("ignoring unknown command '"+ first +"'");
                    }
                }
            } while (!"exit".equals(inputLine));
        } finally { if (scanner != null) scanner.close(); }
        System.out.println();
        
        
//        print1PerLine(tokenToFilePaths.get("study"));
    }
    
    static void printPathsWithAllTerms(String[] tokens) {
       if (tokens.length < 2)
           return;
       Set<String> paths = tokenToFilePaths.get(tokens[1]);
       if (paths == null)
           paths = new LinkedHashSet<String>();
       else paths = new LinkedHashSet<String>(paths); //don't corrupt tokenToFilePaths
       
       for (int i = 2; i < tokens.length; i++) {
           Set<String> newPaths = tokenToFilePaths.get(tokens[i]);
           if (newPaths == null) {
               paths.clear();
               break;
           }
           paths.retainAll(tokenToFilePaths.get(tokens[i]));
       }
       p("Found "+ paths.size() +" results");
       print1PerLine(paths);
    }
    
    static void printPathsWithAnyTerm(String[] tokens) {
       if (tokens.length < 2)
           return;
       Set<String> paths = tokenToFilePaths.get(tokens[1]);
       if (paths == null)
           paths = new LinkedHashSet<String>();
       else paths = new LinkedHashSet<String>(paths); //don't corrupt tokenToFilePaths
       
        for (int i = 2; i < tokens.length; i++) {
           Set<String> newPaths = tokenToFilePaths.get(tokens[i]);
           if (newPaths != null)
               paths.addAll(newPaths);
       }
       
       p("Found "+ paths.size() +" results");
       print1PerLine(paths);
    }

}
