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

public class LexicalAnalyzer {

    private List<String> validTokens;
    private List<Map<Integer, Map<String, String>>> tokensOnRun;

    public LexicalAnalyzer() {
    	
    }
    
    public static List<Map<Integer, Map<String, String>>> compileLexical(String userInput) throws Exception {
        try {
            LexicalAnalyzer lexer = new LexicalAnalyzer();

            return lexer.checkUserInput(userInput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String hasValidCharacters(String token) {
        // Verifique se o token contém apenas letras e números
    	if (token == "//")
    		return "Comment";
    	
    	if (token.matches("\"(.*?)\";?"))
    		return "String value";

    	
    	if (token.matches("if|else|while|for|int|string|float|return|;|\\(|\\)|\\{|\\};?")) {
    		return "System Variable";
    	}
    	else if (token.matches("[+\\-*/><]=?|&&|\\|\\||==|>=|<=|=;?")) {
    		return "Operator";
    	}
    	else if (token.matches("[a-zA-Z][a-zA-Z0-9]*;?")) {
    		return "User Variable";
    	}
    	else if (token.matches("-?\\d+(\\.\\d+)?;?")) {
    		return "Number";
    	}
    		
    	return "";	
    }

    public List<Map<Integer, Map<String, String>>> checkUserInput(String userInput) throws Exception {
        // Divida a entrada do usuário em tokens usando espaços como delimitadores
    	String regex = "^[\\p{L}\\p{N}\\s]+$";
    	String[] lines = userInput.split("\\r?\\n");
    	Integer lineCounter = 0;
    	tokensOnRun = new ArrayList<Map<Integer, Map<String, String>>>();
    	String type = "";
    	
    	for (String line : lines) {
            String[] userTokens = line.split("\\s+");
            lineCounter++;

            // Verifique cada token da entrada do usuário
            for (String userToken : userTokens) {
            	
            	if (userToken.isEmpty() || userToken.isBlank())
            		break;
            	
            	type = hasValidCharacters(userToken);
            	
            	if (type == "Comment")
            		break;
            		
                if (!type.isEmpty()) {
                	HashMap<String, String> map = new HashMap<String, String>();
                	HashMap<Integer, Map<String, String>> mapLine = new HashMap<Integer, Map<String, String>>();
                	map.put(type, userToken);
                	mapLine.put(lineCounter, map);
                    tokensOnRun.add(mapLine);
                } else {
                    throw new Exception("Token inválido na linha " + lineCounter + ": " + userToken);
                }
            }
            if (type == "Comment")
        		break;
    	}
    	
        return tokensOnRun;
    }
}
