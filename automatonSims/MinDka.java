//EXAMPLE INPUT
//1,2,3
//a,b,c
//2,3
//1
//1,a->2
//1,b->1
//1,c->1
//2,a->3
//2,b->3
//2,c->3
//3,a->1
//3,b->3
//3,c->2

package automatonSims;

//import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class MinDka {
	
	static Map<String, Map<String, String>> transitionsTable = new HashMap<>();
	static Set<String> acceptableStates = new TreeSet<>();
	static Set<String> states = new TreeSet<>();
	static Set<String> symbols = new TreeSet<>();
	static String startingState = new String();
	
	public static void main(String[] args) throws FileNotFoundException {	
		//Scanner sc = new Scanner(new File("input4.txt"));
		Scanner sc = new Scanner(System.in);
		
		//obrada prve linije - SKUP STANJA
		String line = sc.nextLine();
		for(String s : line.split(",")) {
			states.add(s);
		}
		
		//obrada druge linije - SKUP MOGUCIH SIMBOLA
		line = sc.nextLine();
		for(String s : line.split(",")) {
			symbols.add(s);
		}
			
		//obrada trece linije - SKUP PRIHVATLJIVIH STANJA
		line = sc.nextLine();
		for(String s : line.split(",")) {
			acceptableStates.add(s);
		}
				
		//obrada cetvrte linije - POCETNO STANJE
		startingState = sc.nextLine();
		
		//obrada od pete linije - TABLICA PRIJELAZA
		while(sc.hasNextLine() && !(line = sc.nextLine()).isEmpty()) {
			int ind1, ind2;
			String currentState = new String();
			String givenSymbol = new String();
			String nextState;
			
			ind1 = line.indexOf(",");
			currentState = line.substring(0, ind1);
			ind2 = line.indexOf("->");
			givenSymbol = line.substring(ind1+1, ind2);
			nextState = line.substring(ind2+2);
			
			if(transitionsTable.containsKey(currentState)) {
				transitionsTable.get(currentState).put(givenSymbol, nextState);
			} else {
				Map<String,String> pair = new HashMap<>();
				pair.put(givenSymbol, nextState);
				transitionsTable.put(currentState, pair);
			}
		}
		sc.close();
		
		removeUnreachableStates();
		removeEquivalentStates();
		printDKA();
	}
	
	public static void removeUnreachableStates() {
		Set<String> DS = new HashSet<>(); //skup dohvatljivih stanja
		Set<String> alreadyAdded = new HashSet<>();
		Queue<String> q = new LinkedList<>();
		
		DS.add(startingState);
		alreadyAdded.add(startingState);
		q.add(startingState);
		
		//napuni DS sa prihvatljivim stanjima
		while(!q.isEmpty()) {
			String currCheckingState = q.poll();
			//za trenutno stanje iz queuea prodi kroz sva njegova moguca nova stanja
			if(transitionsTable.get(currCheckingState) != null) {
				for(String s : transitionsTable.get(currCheckingState).values()) {
					if(!alreadyAdded.contains(s)) {
						DS.add(s);
						alreadyAdded.add(s);
						q.add(s);
					}		
				}
			}
		}
		//napuni NDS s nedohvatljivim stanjima
		Set<String> NDS = new HashSet<>();
		for(String s : states) {
			if(!DS.contains(s)) {
				NDS.add(s);
			}
		}
		//makni nedohvatljiva stanja iz skupa stanja
		states = DS;
		//makni nedohvatljiva stanja iz skupa prihvatljivih stanja
		acceptableStates.removeAll(NDS);
		//makni nedohvatljiva stanja iz tablice prijelaza
		for(String s : NDS) {
			transitionsTable.remove(s);
		}
	}
	

	public static void removeEquivalentStates() {
		//napravi parove kandidata t.d. je par abecedan i da su oba stanja jednaka po prihvatljivosti
		List<List<String>> candidates = new LinkedList<>();
		for(String s1 : states) {
			for(String s2 : states) {
				if(s1.compareTo(s2) < 0 && ((acceptableStates.contains(s1) && acceptableStates.contains(s2)) || 
						     	(!acceptableStates.contains(s1) && !acceptableStates.contains(s2)))) {
					List<String> newCandidate = new LinkedList<>();
					newCandidate.add(s1);
					newCandidate.add(s2);
					candidates.add(newCandidate);
				}
			}
		}
		
		//algoritam - big boy koji sve radi (1. iz knjige)
		Set<List<String>> equivalentStates = new HashSet<>();
		while(!candidates.isEmpty()) {
			Queue<List<String>> q = new LinkedList<>();
			//kreni provjeravati prvi iduci par
			q.add(candidates.get(0));
			List<List<String>> usedSoFar = new LinkedList<>();
			usedSoFar.add(candidates.get(0));
			while(!q.isEmpty()) {
				//ovo je par koji trenutno obradujem
				List<String> workingPair = q.poll();
				//ovo je lista parova koje sam do sada koristio
				//usedSoFar.add(workingPair); //!!!!mozda ovo treba gore
				//ovim pamtim jesam li sto dodao na obradu u ovom koraku algoritma
				boolean addedSomething = false;
				boolean broken = false;
				//za svaki simbol abecede nadi nove parove(first, second)
				for(String s : symbols) {
					String first = new String();
					String second = new String();
					if(transitionsTable.get(workingPair.get(0)) != null) {
						first = transitionsTable.get(workingPair.get(0)).get(s);
					} else {
						first = workingPair.get(0);
					}
					if(transitionsTable.get(workingPair.get(1)) != null) {
						second = transitionsTable.get(workingPair.get(1)).get(s);
					} else {
						second = workingPair.get(1);
					}
					List<String> newPair = new LinkedList<>();
					//ovaj if je radi ocuvanja leksikografskog poredka
					if(first.compareTo(second) < 0) {
						newPair.add(first);
						newPair.add(second);
					} else {
						newPair.add(second);
						newPair.add(first);
					}
					//ako novi par nije jednak po prihvatljivosti - par s kojim je sve krenulo nije istovjetan
					if((acceptableStates.contains(first) && !acceptableStates.contains(second)) ||
							(!acceptableStates.contains(first) && acceptableStates.contains(second))) {
						candidates.remove(usedSoFar.get(0));
						broken = true;
						break;
						//inace ako su novi par 2 razlicita stanja i ako taj par vec nije bio na obradi, dodaj ga u obradu
					} else if(!first.equals(second) && !usedSoFar.contains(newPair)) {
						addedSomething = true;
						q.add(newPair);
						usedSoFar.add(newPair);
					}
				}
				if(broken) break;
				//ako sam prosao kroz sve simbole i nisam dodao nove parove - svi iz usedSoFar su istovjetni
				if(!addedSomething && q.isEmpty()) {
					equivalentStates.addAll(usedSoFar);
					candidates.removeAll(usedSoFar);
				}
			}
		}

		//updateaj/reduciraj listu stanja i prihvatljivih stanja i transitions table
		Set<String> lexOrderEqStates = new TreeSet<>();
		for(List<String> l : equivalentStates) {
			String s = l.toString();
			lexOrderEqStates.add(s.substring(1, s.length()-1));
		}
		NavigableSet<String> reverseOrder = ((TreeSet<String>) lexOrderEqStates).descendingSet();
		for(String s : reverseOrder) {
			String state1 = s.split(", ")[0];
			String state2 = s.split(", ")[1];
			//zamijeni svaki state2 sa state1
			if(startingState.equals(state2)) startingState = state1;
			states.remove(state2);
			acceptableStates.remove(state2);
			transitionsTable.remove(state2);
			for(Map<String, String> m : transitionsTable.values()) {
				for(String smb : symbols) {
					if(m.get(smb).equals(state2)) {
						m.put(smb, state1);
					}
				}
			}	
		}
	}
	
	public static void printDKA() {
		StringBuilder sb = new StringBuilder();
		for(String s : states) {
			sb.append(s).append(",");
		}
		if(sb.length() > 0) System.out.println(sb.toString().substring(0, sb.length()-1));
		else System.out.println();
		sb.delete(0, sb.length());

		for(String s : symbols) {
			sb.append(s).append(",");
		}
		if(sb.length() > 0) System.out.println(sb.toString().substring(0, sb.length()-1));
		else System.out.println();
		sb.delete(0, sb.length());

		for(String s : acceptableStates) {
			sb.append(s).append(",");
		}
		if(sb.length() > 0) System.out.println(sb.toString().substring(0, sb.length()-1));
		else System.out.println();
		
		System.out.println(startingState);
		
		Set<String> transitions = new TreeSet<>();
		for(String state : states) {
			Map<String,String> map = transitionsTable.get(state);
			for(String sym : symbols) {
				sb.delete(0, sb.length());
				sb.append(state).append(",").append(sym).append("->").append(map.get(sym));
				transitions.add(sb.toString());
			}
		}
		for(String s : transitions) {
			System.out.println(s);
		}
	}
	
}
