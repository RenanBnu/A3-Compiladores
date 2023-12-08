package br.trabalhocompiladores.backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SyntacticAnalyzer {
	
	private static List<Map<String, String>> grammar;
	
	private int pendingClosingPar = 0;
	private int pendingClosingCol = 0;
	private int pendingClosingKey = 0;
	
	public static boolean compileSyntatic(String userInput, List<Map<Integer, Map<String, String>>>  tokensByLine) throws Exception {
        try {
        	SyntacticAnalyzer syntatic = new SyntacticAnalyzer();
        	String filePath = "C:/Users/Renan/Downloads/A3_Gabriel_Renan_Compiladores/grammar.txt";

            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            
            grammar = new ArrayList<Map<String, String>>();

            // Loop para ler cada linha do arquivo
            while ((line = reader.readLine()) != null) {
            	HashMap<String, String> map = new HashMap<String, String>();
            	String[] parts = line.split(":");
            	map.put(parts[0].replaceAll(" ", ""), parts[1].replaceAll(" ", ""));
            	grammar.add(map);
            }

            return syntatic.checkTokens(userInput, tokensByLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public boolean checkTokens(String userInput, List<Map<Integer, Map<String, String>>>  tokensByLine) throws Exception {
        		
		int tokensByLine1 = 0;
		String regex = "";
		String key = "";
		String tokensLeft = "";
		boolean endAsKey = false;
		
		
		String[] lines = userInput.split("\\r?\\n");
		
		for (String line : lines) {
			String[] userTokens = line.split("\\s+");

			 for (String userToken : userTokens) {
				 tokensByLine1++;
				 
				 for (Map<String, String> reserved : grammar) {
 				
					 for (Map.Entry<String, String> reservedWord : reserved.entrySet()) {
 						key = reservedWord.getKey();
 						regex = reservedWord.getValue();
 						
 						if (key.equals(userToken)) {
 							for (int i = tokensByLine1; i < userTokens.length; i++) {
 								
 								if ((i == userTokens.length -1) && userTokens[i].equals("{")) {
 									endAsKey = true;
 									break;
 								}
 								tokensLeft += userTokens[i];
 					        }
 								
 							
					        if (!tokensLeft.matches(regex)) {
					        	throw new Exception("Sintático - Código inválido na linha " + line + ": Token esperado após " + userToken);
					        }	
					        else if ((userToken.equals("if") || userToken.equals("else") || userToken.equals("while") ) && !endAsKey) {
					        	throw new Exception("Sintático - Código inválido na linha " + line + ": Token esperado após " + userToken);
					        }
					        else if (endAsKey && (!userToken.equals("if") && !userToken.equals("else") && !userToken.equals("while"))){
					        	throw new Exception("Sintático - Código inválido na linha " + line + ": Token esperado após " + userToken);
					        }
 						}
 						endAsKey = false;
 						tokensLeft = "";
 					}
 				}
				 solvePendencies(userToken);
			 }
			 tokensByLine1 = 0;
		}	
		
		if (pendingClosingPar > 0)
			throw new Exception("Sintático - Esperado )");
		
		if (pendingClosingCol > 0)
			throw new Exception("Sintático - Esperado ]");
		
		if (pendingClosingKey > 0)
			throw new Exception("Sintático - Esperado }");
    	
        return true;
    }
	
	public void solvePendencies(String tokenId) {
		if (tokenId.equals("(")) {
			pendingClosingPar++;
		}
		
		if (tokenId.equals("[")) {
			pendingClosingCol++;
		}
		
		if (tokenId.equals("{")) {
			pendingClosingKey++;
		}
		if (tokenId.equals(")")) {
			pendingClosingPar--;
		}
		
		if (tokenId.equals("]")) {
			pendingClosingCol--;
		}
		
		if (tokenId.equals("}")) {
			pendingClosingKey--;
		}
	}
}



   
