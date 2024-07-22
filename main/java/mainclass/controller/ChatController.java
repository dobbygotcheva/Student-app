package mainclass.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class ChatController {

    private List<mainclass.model.Message> messageList = new ArrayList<>();
    private Map<String, String> phrases = new HashMap<>();

    public ChatController() {
        // Initialize phrases
        phrases.put("Hallo", "Hallo! Wie könnte ich Ihnen heute helfen?");
        phrases.put("Wie geht es Ihnen?", "Mir geht es gut, danke!");
        phrases.put("Da liegt der Hase im Pfeffer.", "That's where the problem lies. (Literally: That's where the rabbit is in the pepper.)");
        phrases.put("Das ist nicht mein Bier.", "That's not my problem. (Literally: That's not my beer.)");
        phrases.put("Das ist mir Wurst.", "I don't care. (Literally: That's sausage to me.)");
        phrases.put("Tomaten auf den Augen haben.", "To be oblivious to something. (Literally: To have tomatoes on one's eyes.)");
        phrases.put("Ich drücke dir die Daumen.", "I'll keep my fingers crossed for you.");
        phrases.put("Jetzt haben wir den Salat.", "Now we've made a mess of things. (Literally: Now we have the salad.)");
        phrases.put("Hals über Kopf.", "Head over heels. (Literally: Neck over head.)");
        phrases.put("Das geht mir auf den Keks.", "That annoys me. (Literally: That gets on my cookie.)");
        phrases.put("Die Kuh vom Eis holen.", "To resolve a difficult situation. (Literally: To get the cow off the ice.)");
        phrases.put("Jetzt mal Butter bei die Fische.", "Let's get down to business. (Literally: Now butter the fish.)");
        phrases.put("Wie alt sind Sie?", "Man stellt solche Fragen nicht!");
        phrases.put("Gericht mit Trauben.", "Nein, nein, das ist zu kreativ!");
        phrases.put("Was ist der Sinn des Lebens?", "Bitte, konzentrieren Sie sich!");
        phrases.put("Was ist die Lieblingsnummer der Deutschen?","Ich weiß es nicht. Lenken Sie mich nicht ab!");
        phrases.put("nein","Nein, nein, das ist ein Wortspiel. Ich hätte es nicht raten können.");
        phrases.put("Danke!", "Bitte!");
    }

    @GetMapping("/chat")
    public String greetingForm(Model model) {
        model.addAttribute("userInput", "");
        model.addAttribute("messages", messageList);
        return "chat";
    }

    @PostMapping("/chat")
    public String greetingSubmit(@RequestParam(name = "userInput", required = false, defaultValue = "") String userInput, Model model) {
        // Add the user and bot messages to the messages list
        messageList.add(new mainclass.model.Message(userInput, true));

        String botResponse = findClosestMatch(userInput); // Add your method to generate bot response here
        messageList.add(new mainclass.model.Message(botResponse, false));

        model.addAttribute("messages", messageList);

        return "redirect:chat"; // Render the chat view itself
    }

    // Find closest match to user input
    private String findClosestMatch(String userInput) {
        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (String phrase : phrases.keySet()) {
            int distance = levenshteinDistance(userInput.toLowerCase(), phrase.toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = phrase;
            }
        }

        return phrases.getOrDefault(closestMatch, "Entschuldigung, ich habe Ihre Anfrage nicht verstanden.");
    }

    // Levenshtein distance algorithm
    private int levenshteinDistance(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1] + 1,
                            Math.min(dp[i][j - 1] + 1, dp[i - 1][j] + 1));
                }
            }
        }

        return dp[len1][len2];
    }
}