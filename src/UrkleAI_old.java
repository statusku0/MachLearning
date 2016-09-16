import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class UrkleAI_old {
	public static int num_of_games = 2000;
	// play against itself
	/* keep track of 3 elements per situation:
	 * 1. call urkel or not [call, don't call]
	 * 2. value of card chosen/placed
	 * 3. draw from deck or pile [deck, pile]
	 * 
	 * 3 elements that decide situation:
	 * 1. composition of pile (determines which situation to consider)
	 * 2. // bottom of deck (worry about this later) 
	 * 3. composition of hand
	 * 
	 * 
	 * 1 case where game is ended prematurely
	 * 1. urk_deck is used up entirely
	 * 
	 * Priority: get hand total down as fast as possible
	 */
	
	HashMap<ArrayList<Deck.Card>, HashMap<Deck, double[]>> pile_map = 
			new HashMap<ArrayList<Deck.Card>, HashMap<Deck, double[]>>();
	
	public UrkleAI_old() {
		double[] def_moveset = new double[3];
		def_moveset[0] = 10;
		def_moveset[1] = 0;
		def_moveset[2] = 50;
		
		HashMap<Deck, double[]> map = new HashMap<Deck, double[]>(); 
		map.put(new Deck(0), def_moveset);
		
		ArrayList<Deck.Card> init_hand = new ArrayList<Deck.Card>();
		
		pile_map.put(init_hand, map);
	}
	
	public double comparePiles(Deck pile1, Deck pile2) {
		int amount_score = Math.abs(pile1.sizeOfDeck() - pile2.sizeOfDeck());
		int value_score = Math.abs(pile1.getValueOfWholeDeck() 
				- pile2.getValueOfWholeDeck());
		
		
		return (amount_score / 70.0) + (value_score / 52.0);
	}
	
	public double compareHands(ArrayList<Deck.Card> hand1, ArrayList<Deck.Card> hand2) {
		int amount_score = Math.abs(hand1.size() - hand2.size());
		int hand1total = 0;
		int hand2total = 0;
		
		for (Deck.Card card : hand1) {
			hand1total += card.getValue();
		}
		
		for (Deck.Card card : hand2) {
			hand2total += card.getValue();
		}
		
		HashSet<Deck.Card> hand1set = new HashSet<Deck.Card>(hand1);
		HashSet<Deck.Card> hand2set = new HashSet<Deck.Card>(hand2);
		
		if (hand1set.size() < hand1.size() && hand2set.size() < hand2.size()) {
			amount_score *= 0.5;
		}
		
		
		double value_score = Math.abs(hand1total - hand2total) / 5.0;
		
		return (amount_score / 5.0) + (value_score / 10);
	}
	
	public int handSum(ArrayList<Deck.Card> hand) {
		int total = 0;
		
		for (Deck.Card card : hand) {
			total += card.getValue();
		}
		
		return total;
	}
	
	public double[] getMoveSet(ArrayList<Deck.Card> hand1, Deck pile1) {
		double min_score_hand = 10;
		double min_score_pile = 10;
		ArrayList<Deck.Card> best_hand = new ArrayList<Deck.Card>();
		
		double[] moveset = new double[3];
		
		for (ArrayList<Deck.Card> hand : pile_map.keySet()) {
			double hand_score = compareHands(hand1, hand);
			if (hand_score < min_score_hand) {
				min_score_hand = hand_score;
				best_hand = hand;				
			}
		}
		
		for (Deck pile : pile_map.get(best_hand).keySet()) {
			double pile_score = comparePiles(pile1, pile);
			if (pile_score < min_score_pile) {
				min_score_pile = pile_score;
				moveset = pile_map.get(best_hand).get(pile);
			}
		}
		
		return moveset;
	}
	
	public void addMoveset(ArrayList<Deck> pile_history, 
			ArrayList<ArrayList<Deck.Card>> hand_history, 
			ArrayList<int[]> movesets, int lossorwin, int handvalue) {
		
		for (int k1 = 0; k1 < pile_history.size(); k1++) {
			Deck current_pile = pile_history.get(k1);
			
			
			if (k1 / 2 >= movesets.size())
				continue;
			
			int[] current_moveset = movesets.get(k1 / 2); 
			ArrayList<Deck.Card> current_hand = hand_history.get(k1 / 2);
			
			double[] moveset = new double[3];
			moveset[0] = 10;
			moveset[1] = 0;
			moveset[2] = 50;
			
			for (ArrayList<Deck.Card> hand : pile_map.keySet()) {
				if (compareHands(hand, current_hand) < 1) {
					for (Deck pile : pile_map.get(hand).keySet()) {
						if (comparePiles(pile, current_pile) < 1) {
							moveset = pile_map.get(hand).get(pile);
							current_pile = pile;
							break;
						}
					}
					current_hand = hand;
					break;
				}
			}
			
			double reward = 80 - handvalue;
			double punishment = handvalue; 
			
			if (lossorwin == 0) {
				// loss
				if (current_moveset[0] == 0) {
					moveset[0] += 0.7 * reward;
				} else {
					moveset[0] -= 0.7 * reward;
				}

				if (current_moveset[2] == 0) {
					moveset[2] += reward;
				} else {
					moveset[2] -= reward;
				}
			} else {
				// win
				if (current_moveset[0] == 0) {
					moveset[0] -= 0.7 * punishment;
				} else {
					moveset[0] += 0.7 * punishment;
				}

				if (current_moveset[2] == 0) {
					moveset[2] -= punishment;
				} else {
					moveset[2] += punishment;
				}
			}
				
			
			if (moveset[0] >= 100) moveset[0] = 90;
			if (moveset[2] > 100) moveset[2] = 100;
			if (moveset[0] <= 0) moveset[0] = 10;
			if (moveset[2] < 0) moveset[2] = 0;
			
			// change chosen card if won
			if (lossorwin == 1) {
				moveset[1] = current_moveset[1];
			}
			
			if (!pile_map.keySet().contains(current_hand)) {
				pile_map.put(current_hand, new HashMap<Deck, double[]>());
			} else if (!pile_map.get(current_hand).keySet().contains(current_pile)) {
				pile_map.get(current_hand).put(current_pile, new double[3]);
			}
			
			pile_map.get(current_hand).put(current_pile, moveset);
		}
	}

	public static void main(String[] args) {
		UrkleAI_old alpha_urkel = new UrkleAI_old();
		
		int win_cnt = 0;
		
		double avg_hand_value = 0;
		int counter = 0;
		
		double avg_card_count_per_hand = 0;
		
		for (int k1 = 0; k1 < num_of_games; k1++) {

			Urkle game = new Urkle(2);

			int move_cnt = 0;
			int player = 0;
			int loss_or_win = 0;

			ArrayList<Deck> pile_history = new ArrayList<Deck>();
			ArrayList<ArrayList<Deck.Card>> player1_hand_history = 
					new ArrayList<ArrayList<Deck.Card>>();
			ArrayList<int[]> player1_movesets = new ArrayList<int[]>();
			ArrayList<ArrayList<Deck.Card>> player2_hand_history = 
					new ArrayList<ArrayList<Deck.Card>>();
			ArrayList<int[]> player2_movesets = new ArrayList<int[]>();
			int player1_lossorwin = 0;
			int player2_lossorwin = 0;
			
			int player1_hand_value = 0;
			int player2_hand_value = 0;

			while (true) {
				if (game.urk_deck.sizeOfDeck() == 0) {
					break;
				}

				move_cnt++;

				pile_history.add(game.pile);
				
				if (player == 0) 
					player1_hand_history.add(game.player_hand_list.get(player));
				else if (player == 1) 
					player2_hand_history.add(game.player_hand_list.get(player));
				
				int[] actual_move = new int[3];

				double[] optimal_move = alpha_urkel.getMoveSet(
						game.player_hand_list.get(player), game.pile);

				double random_value = Math.random() * 100 + 1;

				// call urkel or not
				if (move_cnt > game.num_of_players * 2) {
					// decide to call urkel or not
					if (random_value < optimal_move[0]) {
						// call urkel
						actual_move[0] = 1;
						loss_or_win = game.callUrkel(player);
						
						player1_hand_value = alpha_urkel.handSum(game.player_hand_list.get(0));
						player2_hand_value = alpha_urkel.handSum(game.player_hand_list.get(1));
						
						counter += 2;
						avg_hand_value += player1_hand_value;
						avg_hand_value += player2_hand_value;
						avg_card_count_per_hand += game.player_hand_list.get(0).size();
						avg_card_count_per_hand += game.player_hand_list.get(1).size();

						if (player == 0) {
							player1_movesets.add(actual_move);
						} else if (player == 1) {
							player2_movesets.add(actual_move);
						}
						System.out.println("Number of moves : " + move_cnt);

						break;
					} 
				}


				// choose card(s) from hand
				if (k1 % 10 == 0) actual_move[1] = (int)
						Math.random() * (game.player_hand_list.get(player).size() - 1);
				else {
					actual_move[1] = (int) (Math.random() * 
							(game.player_hand_list.get(player).size() - 1) + optimal_move[1]);
					if (actual_move[1] > (game.player_hand_list.get(player).size() - 1)) {
						actual_move[1] = game.player_hand_list.get(player).size() - 1;
					}
				}

				ArrayList<Deck.Card> hand = game.player_hand_list.get(player);
				ArrayList<Deck.Card> card_list = new ArrayList<Deck.Card>();
				Deck.Card card = hand.get((int) actual_move[1]);

				card_list.add(card);

				// add/remove duplicate cards, if any
				ArrayList<Deck.Card> removal_list = new ArrayList<Deck.Card>();
				for (Deck.Card hand_card : hand) {
					if (hand_card.getValue() == card.getValue()) {
						card_list.add(hand_card);
						removal_list.add(hand_card);
					}
				}
				for (Deck.Card rem_card : removal_list) {
					hand.remove(rem_card);
				}

				// draw from deck or pile
				if (random_value < optimal_move[2]) {
					game.placeAndDraw(hand, card_list, "deck");
				} else {
					actual_move[2] = 1;
					game.placeAndDraw(hand, card_list, "pile");
				}

				if (player == 0) {
					player1_movesets.add(actual_move);
				} else if (player == 1) {
					player2_movesets.add(actual_move);
				}

				player = (player + 1) % game.num_of_players;

			}
			
			if (loss_or_win == 1) {
				win_cnt++;
			}

			if (player == 0) {
				player1_lossorwin = loss_or_win;
				player2_lossorwin = (loss_or_win + 1) % 2;
			} else if (player == 1) {
				player2_lossorwin = loss_or_win;
				player1_lossorwin = (loss_or_win + 1) % 2;
			}
			
			System.out.println("Adding data...");
			// add data
			int hand_value_diff = Math.abs(player1_hand_value - player2_hand_value);
			alpha_urkel.addMoveset(pile_history, player1_hand_history, 
					player1_movesets, player1_lossorwin, hand_value_diff);
			alpha_urkel.addMoveset(pile_history, player2_hand_history, 
					player2_movesets, player2_lossorwin, hand_value_diff);
			System.out.println("Game " + (k1 + 1) + " finished.");
			System.out.println();

		}
		
		System.out.println("Urkel wins: " + win_cnt + "/" + num_of_games);
		
		System.out.println("Avg Hand Value: " + (avg_hand_value / counter));
		System.out.println("Avg Card Count per Hand: " + (avg_card_count_per_hand / counter));
		System.out.println("Avg Card Value in finishing hand: " + 
				(avg_hand_value / avg_card_count_per_hand));
		
	}
}
