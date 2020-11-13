import java.util.ArrayList;
import java.util.List;

public class Hangman {
    private static String secretWord;
    private static List<String> word = new ArrayList<String>();
    private static List<String> playerWord = new ArrayList<String>();

    public static void startGame() {
	// First server picks a word and makes its reference
	secretWord = Words.Selector();
	word = List.of(secretWord.split(""));
	// Convert char array to blank spaces to player
	for (int i = 0; i < word.size(); i++) {
	    if (word.get(i).trim().length() > 0) {
		playerWord.add(word.get(i));
	    }
	    else {
		playerWord.add(" ");
	    }
	}

    }

    public static List<String> sendGameText() {
	return playerWord;
    }
}