import java.util.*;

public class Deck {
		String[] suits = {"Club", "Diamond", "Heart", "Spade"};
		String[] nums = {"Ace", "2", "3", "4", "5", 
				"6", "7", "8", "9", "10", "Jack", "Queen", "King", "Joker", "Joker"};
		Stack<Card> list_cards;
		
		public class Card implements Comparator<Card>{
			String Number;
			String Suit;
			
			public Card(String num, String suit) {
				Number = num;
				Suit = suit;
			}
			
			public Card(String num) {
				Number = num;
			}
			
			public String getNumber() {
				return Number;
			}
			
			public String getSuit() {
				return Suit;
			}
			
			public String showCard() {
				if (Number.equals("Joker")) {
					return Number;
				}
				return Number + " of " + Suit + "s"; 
			}
			
			public int getValue() {
				for (int k1 = 0; k1 < nums.length; k1++) {
					if (Number.equals(nums[k1]) && !Number.equals("Joker")) {
						return k1 + 1;
					}
				}
				return 0;
			}
			
			public int compare(Card card1, Card card2) {
				return card1.getValue() - card2.getValue();
			}
		}
		public Deck(){
			list_cards = new Stack<Card>();
			for (int k1 = 0; k1 < nums.length; k1++) {
				if (nums[k1].equals("Joker")) {
					list_cards.add(new Card(nums[k1]));
				} else {
					for (int k2 = 0; k2 < suits.length; k2++) {
						list_cards.add(new Card(nums[k1], suits[k2]));
					}
				}
			}
		}
		
		// instantiate an empty deck
		public Deck(int num) {
			list_cards = new Stack<Card>();
		}
		
		public Deck(Stack<Card> stack) {
			list_cards = stack;
		}

		public int sizeOfDeck() {
			return list_cards.size();
		}
		
		public Stack<Card> shuffleDeck() {
			Collections.shuffle(list_cards);
			return list_cards;
		}
		
		public Card drawFromDeck() {
			return list_cards.pop();
		}
		
		public ArrayList<Card> drawMultipleFromDeck(int num_of_cards) {
			ArrayList<Card> result = new ArrayList<Card>();
			for (int k1 = 0; k1 < num_of_cards; k1++) {
				result.add(list_cards.pop());
			}
			
			return result;
		}
		
		public void addToTopOfDeck(Card card) {
			list_cards.add(card);
		}
		
		public void showDeck() {
			for (Card card : list_cards) {
				ArrayList<String> card_details = new ArrayList<String>();
				card_details.add(card.getNumber());
				if (!card.getNumber().equals("Joker")) 
					card_details.add(card.getSuit());
				System.out.println(card_details);
			}
		}
		
		public Card getCardAtBottomOfDeck() {
			@SuppressWarnings("unchecked")
			Stack<Card> temp_deck = (Stack<Card>) list_cards.clone();
			
			while (temp_deck.size() > 1) {
				temp_deck.pop();
			}
			
			return temp_deck.pop();
		}
		
		public int getValueOfWholeDeck() {
			int sum = 0;
			for (Card card : list_cards) {
				sum += card.getValue();
			}
			
			return sum;
		}
		
	}