//EXAMPLE INPUT
//a,b,a
//s1,s2,s3,s4,s5
//a,b,c
//s2,s5
//s1
//s1,a->s2
//s1,b->s5
//s1,c->s3
//s2,a->s3
//s2,b->s1
//s2,c->s4
//s3,a->s4
//s3,b->s2
//s3,c->s4
//s4,a->s5
//s4,b->s3
//s4,c->s1
//s5,a->s1
//s5,b->s4
//s5,c->s1

package automatonSims;

//import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class SimEnka {
	
	private static TreeSet<String> epsilonResult = new TreeSet<>();
	
	public static void main(String[] args) throws FileNotFoundException {
		//Scanner sc = new Scanner(new File("input2.txt"));
		Scanner sc = new Scanner(System.in);
		
		//obrada prve linije - ULAZNI NIZOVI
		String line = sc.nextLine();
		String[] inputs = line.split("\\|");

		//obrada druge linije - SKUP STANJA
		Set<String> states = new HashSet<>();
		line = sc.nextLine();
		for(String s : line.split(",")) {
			states.add(s);
		}
		states.add("#");

		//obrada trece linije - SKUP MOGUCIH SIMBOLA
		Set<String> symbols = new HashSet<>();
		line = sc.nextLine();
		for(String s : line.split(",")) {
			symbols.add(s);
		}
		symbols.add("$");
	
		//obrada cetvrte linije - SKUP PRIHVATLJIVIH STANJA
		Set<String> acceptableStates = new HashSet<>();
		line = sc.nextLine();
		for(String s : line.split(",")) {
			acceptableStates.add(s);
		}
		
		//obrada pete linije - POCETNO STANJE
		String startingState = sc.nextLine();
		
		//obrada od seste linije - TABLICA PRIJELAZA
		Map<String, Map<String, Set<String>>> transitionsTable = new HashMap<>();
		int ind1, ind2;
		String currentState = new String();
		String givenSymbol = new String();
		String[] nextState;
		
		while(sc.hasNextLine() && !(line = sc.nextLine()).isEmpty()) {
			//line = sc.nextLine();
			ind1 = line.indexOf(",");
			currentState = line.substring(0, ind1);
			ind2 = line.indexOf("->");
			givenSymbol = line.substring(ind1+1, ind2);
			nextState = line.substring(ind2+2).split(",");
			
			if(transitionsTable.containsKey(currentState)) {
				if(transitionsTable.get(currentState).containsKey(givenSymbol)) {
					//ako je za trenutno stanje vec upisan simbol koji se i sada upisuje(npr iz q3 za 1 idemo u q4 i q5)
					for(String ns : nextState) {
						transitionsTable.get(currentState).get(givenSymbol).add(ns);
					}
				} else {
					//ako za trenutno stanje trenutni simbol ne vodi nikud
					Set<String> nextStates = new HashSet<>();
					for(String ns : nextState) {
						nextStates.add(ns);
					}
					transitionsTable.get(currentState).put(givenSymbol, nextStates);
				}
			} else {
				Map<String, Set<String>> pair = new HashMap<>();
				Set<String> nextStates = new HashSet<>();
				for(String ns : nextState) {
					nextStates.add(ns);
				}
				pair.put(givenSymbol, nextStates);
				transitionsTable.put(currentState, pair);
			}
		}
		
		sc.close();
		
		//AUTOMAT
		for(String input : inputs) {
			Queue<TreeSet<String>> mainQueue = new LinkedList<>();
			TreeSet<String> startElement = new TreeSet<>();
			startElement.add(startingState);
			epsilonResult.clear();
			provjeriEpsilon(startingState, transitionsTable);
			startElement.addAll(epsilonResult);
			mainQueue.add(startElement);
			String[] inputSymbols = input.split(",");
			List<String> finalOutput = new LinkedList<>();
			finalOutput.addAll(startElement);
			finalOutput.add("|");
						
			int index = 0;
			String inputSymbol = inputSymbols[index];
				while(!mainQueue.isEmpty()) {
					TreeSet<String> workingElement = mainQueue.poll();
					TreeSet<String> newElement = new TreeSet<>();
					for(String workingState : workingElement) {
						if(transitionsTable.containsKey(workingState)) {
							if(transitionsTable.get(workingState).get(inputSymbol) != null) {
								for(String nxtSt : transitionsTable.get(workingState).get(inputSymbol)) {
									newElement.add(nxtSt);
									epsilonResult.clear();
									provjeriEpsilon(nxtSt, transitionsTable);
									newElement.addAll(epsilonResult);
								}
							} 
						}
					}
					newElement.remove("#");
					if(newElement.isEmpty()) newElement.add("#");
					mainQueue.add(newElement);
					finalOutput.addAll(newElement);
					finalOutput.add("|");
					index++;
					if(index < inputSymbols.length) {
						inputSymbol = inputSymbols[index];
					} else break;
				}
			
			
			for(int i=0; i<finalOutput.size()-1; i++) {
				System.out.print(finalOutput.get(i));
				if(finalOutput.get(i+1) != "|" && finalOutput.get(i) != "|" && i+1 < finalOutput.size()) {
					System.out.print(",");
				}
			}
			System.out.println();
		}
		
	}
	
	public static void provjeriEpsilon(String state, Map<String, Map<String, Set<String>>> transitionsTable) {
		if(transitionsTable.containsKey(state)) {
			if(transitionsTable.get(state).get("$") != null) {
				for(String s : transitionsTable.get(state).get("$")) {
					if(!epsilonResult.contains(s)) {
						epsilonResult.add(s);
						provjeriEpsilon(s, transitionsTable);
					}
				}
			}
		}
	}
}