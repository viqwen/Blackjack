import java.io.*;
import java.util.*;

public class ExtractMethodText {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java ExtractMethodText <SourceFile.java> <MethodName>");
            System.exit(1);
        }

        String filename = args[0];
        String methodName = args[1];

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean found = false;
            int braceCount = 0;
	    
            do { // Cherche une ligne contenant le nom de méthode suivi de (
		line = reader.readLine();
		if (line != null && line.matches(".*\\b" + methodName + "\\s*\\(.*")) found = true;
	    } while (line != null && !found);
	    System.out.println(line);

	    braceCount = 1;	    
	    while ((line != null) && braceCount != 0) {
		line = reader.readLine();
		if (line != null){
		    System.out.println(line);
		    braceCount += countChar(line, '{') - countChar(line, '}');
		}
	    }
	}
    }
    
    private static int countChar(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) count++;
        }
        return count;
    }
}
