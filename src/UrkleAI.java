import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

/* TODO: 
 * - write method to choose MoveSets from the hashmaps
 * - write method to store data for subsequent generations
 * 
 */
	
public class UrkleAI {
	
	Urkle game;
	
	HashMap<Pile, Set> pile_map;
	HashMap<Hand, Set> hand_map;
	HashMap<Deck.Card, Set> bottcard_map;
	HashMap<Integer, Set> move_map;
	
	ArrayList<Pile> pile_array = new ArrayList<Pile>();
	ArrayList<Hand> hand_array = new ArrayList<Hand>();
	ArrayList<Deck.Card> bottcard_array = new ArrayList<Deck.Card>();
	ArrayList<Integer> move_cnt_array = new ArrayList<Integer>();
	ArrayList<MoveSet> moveset_array = new ArrayList<MoveSet>();
	
	public UrkleAI(Urkle g) {
		game = g;
		
		pile_map = new HashMap<Pile, Set>();
		hand_map = new HashMap<Hand, Set>();
		bottcard_map = new HashMap<Deck.Card, Set>();
		move_map = new HashMap<Integer, Set>();
	}
	
	public class Hand {
		
		ArrayList<Deck.Card> hand = new ArrayList<Deck.Card>();
		
		public Hand(ArrayList<Deck.Card> list_cards) {
			hand = list_cards;
		}
		
		public void sortHand() { 
			/* not needed for Urkle.java since hands are automatically
			 * sorted after each move  
			 * 
			 */
			Deck deck = new Deck();
			Collections.sort(hand, deck.new Card("2", "Diamonds"));
		}
		
		public Deck.Card pickRandomCard() {
			
			int rand_num = (int) Math.random() * hand.size();
			
			return hand.get(rand_num);
		}
		
		public int getValue() {
			int sum = 0;
			
			for (Deck.Card card : hand) {
				sum += card.getValue();
			}
			
			return sum;
		}
		
		public Deck.Card get(int index) {
			return hand.get(index);
		}
		
		public int size() {
			return hand.size();
		}
	}
	
	public double compareHands(Hand hand1, Hand hand2) {
		// assumes hands are sorted
		// make both hands 5-card hands by putting in dummy 0 cards
		
		// get avg difference
		
		int[] hand1_value = new int[5];
		int[] hand2_value = new int[5];
		
		for (int k1 = 0; k1 < hand1.size(); k1++) {
			hand1_value[5 - hand1.size() + k1] = hand1.get(k1).getValue(); 
		}
		
		for (int k1 = 0; k1 < hand2.size(); k1++) {
			hand2_value[5 - hand2.size() + k1] = hand2.get(k1).getValue(); 
		}
		
		int diff_sum = 0;
		for (int k1 = 0; k1 < 5; k1++) {
			diff_sum += Math.abs(hand1_value[k1] - hand2_value[k1]);
		}
		
		double avg_diff = diff_sum / 5.0;
		
		// measure value-independent similarity between hands ("shape of curve")
		// can detect similarities between hands with duplicates
		
		int[] diff1 = new int[4];
		
		for (int k1 = 0; k1 < 4; k1++) {
			diff1[k1] = Math.abs(hand1_value[k1 + 1] - hand1_value[k1]);
		}
		
		int[] diff2 = new int[4];
		
		for (int k1 = 0; k1 < 4; k1++) {
			diff2[k1] = Math.abs(hand2_value[k1 + 1] - hand2_value[k1]);
		}
		
		Arrays.sort(diff1);
		Arrays.sort(diff2);
		
		int diff_sum2 = 0;
		for (int k1 = 0; k1 < 4; k1++) {
			diff_sum2 += Math.abs(diff1[k1] - diff2[k1]);
		}
		
		double avg_diff2 = diff_sum2 / 4.0;
		
		double[] result = new double[2];
		result[0] = avg_diff;
		result[1] = avg_diff2;
		
		return 0.3 * result[0] + 0.7 * result[1];
	}
	
	
	public class Pile {
		Deck pile;
		
		public Pile(Deck p) {
			pile = p;
		}				

	}
	
	public double comparePiles(Pile pile1, Pile pile2) {
		// accounts for differing pile sizes
		
		@SuppressWarnings("unchecked")
		Deck pile1_copy = new Deck((Stack<Deck.Card>)pile1.pile.list_cards.clone());
		
		@SuppressWarnings("unchecked")
		Deck pile2_copy = new Deck((Stack<Deck.Card>)pile2.pile.list_cards.clone());
		
		ArrayList<Deck.Card> pile1_array = new ArrayList<Deck.Card>();
		ArrayList<Deck.Card> pile2_array = new ArrayList<Deck.Card>();
		
		for (Deck.Card card : pile1_copy.list_cards) {
			pile1_array.add(card);
		}
		for (Deck.Card card : pile2_copy.list_cards) {
			pile2_array.add(card);
		}
				
		Collections.sort(pile1_array, pile1_copy.new Card("2", "Diamonds"));
		Collections.sort(pile2_array, pile2_copy.new Card("2", "Diamonds"));
		
		double diff_sum = 0;
		int num = Math.min(pile1_array.size(), pile2_array.size());
		for (int k1 = 0; k1 < num; k1++) {
			diff_sum += Math.abs(pile1_array.get(k1).getValue() 
					- pile2_array.get(k1).getValue());
		}
		
		double avg_diff = diff_sum / num;
		
		//TODO: account for order of cards
		
		Deck.Card top_card1 = pile1_copy.list_cards.pop();
		Deck.Card top_card2 = pile2_copy.list_cards.pop();
		
		double top_card_diff = Math.abs(top_card1.getValue() - top_card2.getValue());
		
		
		double[] result = new double[2];
		result[0] = avg_diff;
		result[1] = top_card_diff;
		
				
		return 0.3 * avg_diff + 0.7 * top_card_diff;
	}
	
	public double compareBottomCard(Deck.Card card1, Deck.Card card2) {
		return Math.abs(card1.getValue() - card2.getValue());
	}
	
	public int compareMoves(int num_move1, int num_move2) {
		return Math.abs(num_move1 - num_move2);
	}
	
	public MoveSet generateRandomMoveSet(Hand hand) {
		
		int card_to_place = (int) Math.random() * 4;

		double rand_num = Math.random() * 100 + 1;
		
		String draw_choice = "";
		
		if (rand_num < 50) {
			draw_choice = "deck";
		} else {
			draw_choice = "pile";
		}
		
		return new MoveSet(card_to_place, draw_choice);
	}
	
	
	
	// Storing/Retrieving Data ///////////////////////
	
	public class MoveSet {
		int card_to_place;
		String draw_choice; // deck or pile
		
		
		public MoveSet(int card, String draw) {
			card_to_place = card;
			draw_choice = draw;
		}
	}
	
	
	public class Set {
		HashMap<MoveSet, double[]> movesets_to_result = new HashMap<MoveSet, double[]>();
		
		public Set(HashMap<MoveSet, double[]> movesetstoresult) {
			movesets_to_result = movesetstoresult;
		}
		
		public void remove(MoveSet moveset) {
			movesets_to_result.remove(moveset);
		}
		
	}
	
	public void storeData() {
		
	}
	
	public void initializeData(Hand hand, int move_cnt, MoveSet moveset) {
		Pile pile = new Pile(game.pile);
		Deck.Card bottcard = game.urk_deck.getCardAtBottomOfDeck();
		
		pile_array.add(pile);
		hand_array.add(hand);
		bottcard_array.add(bottcard);
		move_cnt_array.add(move_cnt);
		moveset_array.add(moveset);
		
		if (!pile_map.containsKey(pile))
			pile_map.put(pile, new Set(new HashMap<MoveSet, int[]>()));
		if (!hand_map.containsKey(hand))
			hand_map.put(hand, new Set(new HashMap<MoveSet, int[]>()));
		if (!bottcard_map.containsKey(bottcard))
			bottcard_map.put(bottcard, new Set(new HashMap<MoveSet, int[]>()));
		if (!move_map.containsKey(move_cnt))
			move_map.put(move_cnt, new Set(new HashMap<MoveSet, int[]>()));
		
	}
	
	public void updateResults(double[] result) {
		for (int k1 = 0; k1 < pile_array.size(); k1++) {
			pile_map.get(pile_array.get(k1)).movesets_to_result.put(
					moveset_array.get(k1), result);
			hand_map.get(hand_array.get(k1)).movesets_to_result.put(
					moveset_array.get(k1), result);
			bottcard_map.get(bottcard_array.get(k1)).movesets_to_result.put(
					moveset_array.get(k1), result);
			move_map.get(move_cnt_array.get(k1)).movesets_to_result.put(
					moveset_array.get(k1), result);
		}		
	}
	
	
	//Simulation///////////////////////////
	
	public int simulateTrial(int hand_total_goal) {
		
		// clear history arrays
		pile_array.clear();
		hand_array.clear();
		bottcard_array.clear();
		move_cnt_array.clear();
		moveset_array.clear();
		
		int hand_total = 0;
		int init_hand_total = 0;
		
		int move_count = 0;
		
		while (true) {
			
			// look at hand
			Hand cur_hand = new Hand(game.player_hand_list.get(0));
			
			// compute hand total and determine if goal is met 
			// or if deck/pile is entirely used up
			hand_total = cur_hand.getValue();
			
			if (move_count == 0) init_hand_total = hand_total;
			
			if (hand_total <= hand_total_goal || 
					game.urk_deck.sizeOfDeck() == 0 || 
					game.pile.sizeOfDeck() == 0) break;
			
			// generate MoveSet
			MoveSet cur_move = generateRandomMoveSet(cur_hand);
			
			// store data
			initializeData(cur_hand, move_count, cur_move);
			
			// execute MoveSet
			executeMoveSet(cur_hand, cur_move);
			
			// increment move counter
			move_count++;
			

		}
		
		double[] result = new double[2];
		
		result[1] = move_count;

		System.out.println("Initial Hand Total: " + init_hand_total);
		System.out.println("Final Hand Total:   " + hand_total);
		System.out.println("Num. of Moves:      " + move_count);
		System.out.println();
		
		result[0] = hand_total / (double) init_hand_total;
		
		updateResults(result);
		
		if (result[0] < 1) {
			return 1;
		}
		
		
		return 0;
		
	}
	
	public void executeMoveSet(Hand h, MoveSet moveset) {
		Deck.Card card = h.get((5 - h.size()) + moveset.card_to_place);
		ArrayList<Deck.Card> place_cards = new ArrayList<Deck.Card>();
		for (Deck.Card hcard : h.hand) {
			if (hcard.Number.equals("Joker") && card.Number.equals("Joker")) {
				place_cards.add(card);
			} else if (hcard.Number.equals(card.Number) && hcard.Suit.equals(card.Suit)) {
				place_cards.add(card);
			}
		}
		//System.out.println(moveset.draw_choice);
		//System.out.println("Deck: " + game.urk_deck.sizeOfDeck());
		//System.out.println("Pile: " + game.pile.sizeOfDeck());
		game.placeAndDraw(h.hand, place_cards, moveset.draw_choice);
	}
	
	public MoveSet chooseMoveSet(Pile pile, Hand hand, Deck.Card bottcard, int move_cnt) {
		
		// get pile that best matches pile and get corresponding set
		// then choose best moveset from that set
		Pile best_pile = new Pile(new Deck(0));
		double pile_score = 0;
		
		for (Pile pil : pile_map.keySet()) {
			double p = comparePiles(pile, pil);
			if (best_pile.pile.sizeOfDeck() == 0) {
				best_pile = pil;
				pile_score = p;
			} else if (p < pile_score) {
				best_pile = pil;
			}
		}
		
		// get hand that best matches hand and get corresponding set
		// then choose best moveset from that set
		
		
		
		// get bottcard that best matches bottcard and get corresponding set
		// then choose best moveset from that set
		
		// get move_cnt that best matches move_cnt and get corresponding set
		// then choose best moveset from that set
	}
	
	
	/// Refine Sets //////////////////////////
	
	public void refineSets() {
		for (Pile pile : pile_map.keySet()) {
			
		}
	}
	
	public void refSet(Set set) {
		// refine a single set
		
		
	}
	


	

	
	
	
	public static void main(String[] args) {
		
		int trial_count = 0;
		while (true) {
			System.out.println("Trial: " + trial_count);

			// set up 1-player game
			Urkle game = new Urkle(1);

			// set up Urkle AI
			UrkleAI alpha_urk = new UrkleAI(game);


			int result = alpha_urk.simulateTrial(10);
			
			if (result == 1) break;
			
			trial_count++;
		}
		
		
		
	}
}


