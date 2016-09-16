import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Urkle {
	
	public int num_of_players;
	
	public ArrayList<ArrayList<Deck.Card>> player_hand_list;
	
	public ArrayList<Integer> player_scores;
	
	public static int penalty_points = 20;
	
////////////////////////////////
	public Deck urk_deck;
	public Deck pile;
////////////////////////////////
	
	
	public Urkle(int num_players) {
		// set up deck
		urk_deck = new Deck();
		
		// shuffle deck
		urk_deck.shuffleDeck();
		
		// initialize empty pile and put in card from top of urk_deck
		pile = new Deck(0);
		pile.addToTopOfDeck(urk_deck.drawFromDeck());
						
		num_of_players = num_players;
		
		// initialize player_hand_list
		player_hand_list = new ArrayList<ArrayList<Deck.Card>>();
		
		// initialize player_scores
		player_scores = new ArrayList<Integer>();
		for (int k1 = 0; k1 < num_of_players; k1++) {
			player_scores.add(0);
		}
		
		// deal out 5 cards per player to start game
		for (int k1 = 0; k1 < num_of_players; k1++) {
			player_hand_list.add(urk_deck.drawMultipleFromDeck(5));
			Collections.sort(player_hand_list.get(k1), urk_deck.new Card("2", "Diamonds"));
		}
	}
	
	// making a move
	public void placeAndDraw(ArrayList<Deck.Card> hand, ArrayList<Deck.Card> cards_to_remove, 
			String stack) {
		
		Deck stack_to_draw_from = new Deck(0);
		
		if (stack.equals("deck")) {
			stack_to_draw_from = urk_deck;
		} else if (stack.equals("pile")) {
			stack_to_draw_from = pile;
		}
		
		// remove specified card(s) from hand
		for (Deck.Card card : cards_to_remove)
			hand.remove(card);
		
		// draw from pile into hand
		hand.add(stack_to_draw_from.drawFromDeck());
		Collections.sort(hand, urk_deck.new Card("2", "Diamonds"));
		
		// add specified card(s) to top of pile
		for (Deck.Card card : cards_to_remove)
			pile.addToTopOfDeck(card);
		
	}
	
	// calculate sum of hand
	public int sumOfHand(ArrayList<Deck.Card> hand) {
		int result = 0;
		for (Deck.Card card : hand)
			result += card.getValue();
		
		return result;
	}
	
	// calling urkel
	public int callUrkel(int player) {
		int loss_or_win = 0; // 0 for loss, 1 for win
		
		// reveal hand
		showHand(player);
		
		int player_sum = sumOfHand(player_hand_list.get(player));
		
		int player_list_size = player_hand_list.size();
		
		ArrayList<Integer> player_sums = new ArrayList<Integer>();
		
		for (int k1 = 0; k1 < player_list_size; k1++) {
			int player_hand_sum = sumOfHand(player_hand_list.get(k1));
			player_sums.add(player_hand_sum);
			if ((k1 != player) && (player_hand_sum < player_sum)) {
				// player loses round (gains penalty points)
				System.out.println("Player " + player + " loses :(");
				player_scores.set(player, player_scores.get(player) + penalty_points);
				return loss_or_win;
			}
		}
		
		// else, player wins round and everyone else gains points based on difference
		loss_or_win = 1;
		System.out.println("Ayy player " + player + " wins :D");
		for (int k1 = 0; k1 < player_list_size; k1++) {
			if ((k1 != player)) {
				player_scores.set(k1, player_scores.get(k1) 
						+ player_sums.get(k1) - player_sum);
			}
		}
		
		return loss_or_win;
	}
	
	// show hand
	public void showHand(int player) {
		System.out.println("Player " + player + " Hand:");
		for (Deck.Card card : player_hand_list.get(player)) {
			System.out.println(card.showCard());
		}
		System.out.println();
	}
	
	// show card on top of pile
	public void showTopOfPile() {
		Deck.Card top_card = pile.drawFromDeck();
		System.out.println("Top Of Pile: " + top_card.showCard());
		System.out.println();
		pile.addToTopOfDeck(top_card);
	}
	
	// list out scores
	public void showScores() {
		for (int k1 = 0; k1 < player_scores.size(); k1++) {
			System.out.println("Player " + k1 + ": " + player_scores.get(k1));
		}
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {	
		// ask for number of players
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(in);
				
		System.out.println("Number of Players? : (enter number)");
		
		Urkle game = new Urkle(Integer.parseInt(keyboard.readLine()));
		
		for (int k1 = 0; k1 < game.player_hand_list.size(); k1++) {
			game.showHand(k1);
		}
		
		game.showTopOfPile();
		
		System.out.println("Bottom Card: " + game.urk_deck.getCardAtBottomOfDeck().showCard());
		
		int player_turn = 0;
		
		int move_cnt = 0;
		
		while (true) {
			move_cnt++;
			
			System.out.println("Player " + player_turn + "'s Turn:");
			
			// call urkel?
			if (move_cnt > (game.num_of_players * 2)) {
				System.out.println("Would you like to call urkel? (type 'yes' or 'no')");
				if (keyboard.readLine().equals("yes")) {
					game.callUrkel(player_turn);
					game.showScores();
					break;
				}
			}
			
			
			// get list of cards to remove
			ArrayList<Deck.Card> cards_to_remove = new ArrayList<Deck.Card>();
			while (true) {
				System.out.println("Card to remove (position in hand) (type 'done' when done):");
				String input = keyboard.readLine();
				if (!input.equals("done"))
					cards_to_remove.add(game.player_hand_list.
						get(player_turn).get(Integer.parseInt(input)));
				else if (input.equals("done"))
					break;
				
			}
			
			// make move
			System.out.println("Draw from Deck or Pile?: (type 'deck' or 'pile')");
			String input = keyboard.readLine();
			if (input.equals("deck")) {
				game.placeAndDraw(game.player_hand_list.get(player_turn), 
						cards_to_remove, "deck");
			} else if (input.equals("pile")) {
				game.placeAndDraw(game.player_hand_list.get(player_turn), 
						cards_to_remove, "pile");
			}
			
			// show resultant hand
			game.showHand(player_turn);
			
			// show top card of pile
			game.showTopOfPile();
			
			
			for (int k1 = 0; k1 < game.player_hand_list.size(); k1++) {
				game.showHand(k1);
			}
			
			// move to next player
			player_turn = (player_turn + 1) % game.num_of_players;
			
		}
		
	}

}
