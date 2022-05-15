//EXAMPLE INPUT
//a
//q0
//a
//K
//q0
//q0
//K
//q0,a,K->q0,K

package automatonSims;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class SimPa {
	public static Map<String, String> transitionsTable = new HashMap<>();
	private static Stack<String> stack = new Stack<>();
	private static StringBuilder sb = new StringBuilder();
	private static String workingState;

	public static void main(String[] args) throws FileNotFoundException {
		Scanner sc = new Scanner(new File("primjer.in"));
		//Scanner sc = new Scanner(System.in);
		
		//obrada prve linije - ULAZNI NIZOVI
		String line = sc.nextLine();
		String[] inputs = line.split("\\|");

		//obrada druge linije - SKUP STANJA
		Set<String> states = new HashSet<>();
		line = sc.nextLine();
		for(String s : line.split(",")) {
			states.add(s);
		}

		//obrada trece linije - SKUP MOGUCIH SIMBOLA
		Set<String> symbols = new HashSet<>();
		line = sc.nextLine();
		for(String s : line.split(",")) {
			symbols.add(s);
		}
		symbols.add("$");
		
		//obrada cetvrte linije - SKUP ZNAKOVA STOGA
		Set<String> stackSymbols = new HashSet<>();
		line = sc.nextLine();
		for(String s : line.split(",")) {
			stackSymbols.add(s);
		}
	
		//obrada pete linije - SKUP PRIHVATLJIVIH STANJA
		Set<String> acceptableStates = new HashSet<>();
		line = sc.nextLine();
		for(String s : line.split(",")) {
			acceptableStates.add(s);
		}
		
		//obrada seste linije - POCETNO STANJE
		String startState = sc.nextLine();
		
		//obrada sedme linije - POCETNO STANJE STOGA
		String stackStartState = sc.nextLine();

		
		//obrada od osme linije - TABLICA PRIJELAZA
		while(sc.hasNextLine() && !(line = sc.nextLine()).isEmpty()) {
            String currentState = line.split("->")[0];
            String nextState = line.split("->")[1];
            transitionsTable.put(currentState, nextState);
		}
		
		//------------------------------------------------------AUTOMAT----------------------------------------------------
		for(String input : inputs) {
			sb.setLength(0);
			stack.clear();
			stack.push(stackStartState);
			workingState = startState;
			boolean accepted = true;
			
			sb.append(workingState).append("#").append(stackStartState).append("|");
			String key = new String();
			String[] inputSymbols = input.split(",");
			int index = 0;
			
			while(index < inputSymbols.length) {
				if(!stack.isEmpty()) {
					key = workingState + "," + inputSymbols[index] + "," + stack.peek();
					if(transitionsTable.containsKey(key)) {
						index++;
						calculateTransition(key);
					} else {
						key = workingState + "," + "$" + "," + stack.peek();
						if(transitionsTable.containsKey(key)) {
							calculateTransition(key);
						} else {
							sb.append("fail|");
							accepted = false;
							break;
						}
					}
					
				} else {
					sb.append("fail|");
					accepted = false;
					break;
				}

			}
			//ostali smo bez simbola, sad jos provjeri potencijalne prijelaze za prazni niz
			while(!stack.isEmpty() && !acceptableStates.contains(workingState)) {
				key = workingState + "," + "$" + "," + stack.peek();
				if(transitionsTable.containsKey(key)) {
					calculateTransition(key);
				} else break;
			}
			
			if(accepted && acceptableStates.contains(workingState)) {
				sb.append(1);
			} else sb.append(0);
			
			System.out.println(sb.toString());
		}

	}

	public static void calculateTransition(String key) {
		stack.pop();
		String newTransition = transitionsTable.get(key);
		workingState = newTransition.split(",")[0];
		String newPeek = newTransition.split(",")[1];
		if(!newPeek.equals("$")) {
			char[] peeks = newPeek.toCharArray();
			for(int i = peeks.length-1; i >= 0; i--) {
				stack.push(Character.toString(peeks[i]));
			}
		}
		sb.append(workingState).append("#");
		if(!stack.isEmpty()) {
			Collections.reverse(stack);
			for(String s : stack) {
				sb.append(s);
			}
			Collections.reverse(stack);
			sb.append("|");
		} else {
			sb.append("$|");
		}
	}

}
