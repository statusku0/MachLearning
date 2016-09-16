import java.io.*;

public class BlackJack {
	public static void main(String[] args) throws IOException {
		Deck testDeck = new Deck();
		
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(in);
		
		System.out.println("Play BlackJack (type 'start'):");
		
		if (keyboard.readLine().equals("start")) {
			int playertotal = 0;
			
			String player_input = "hit";
			while (player_input.equals("hit")) {
				testDeck.shuffleDeck();
				
				Deck.Card player_card = testDeck.drawFromDeck();
				
				System.out.println("Here's your card: " + 
						player_card.showCard());
				
				playertotal += player_card.getValue();
				
				System.out.println("Your total is: " + playertotal);
				
				if (playertotal > 21) {
					System.out.println("ggs u lost lol");
					break;
				} else {
					System.out.println("Would you like to hit or stay?:");
					player_input = keyboard.readLine();
					
					if (player_input.equals("stay")) {
						System.out.println("Your total is: " + playertotal);
						break;
					}
				}
				
			}
		}
	}

}
