package com.journalinsight.backend.service;

import com.journalinsight.backend.dto.EmotionDetectionResult;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmotionClassifierService {

    // ── Improvement 1: Stop words ─────────────────────────────────────────────
    // Only true function words: pronouns, auxiliaries, prepositions, conjunctions.
    // Content words (good, well, want, think, etc.) are kept — they carry emotional signal.
    private static final Set<String> STOP_WORDS = Set.of(
        // Pronouns
        "i", "me", "my", "myself", "we", "our", "you", "your",
        "he", "him", "his", "she", "her", "it", "its",
        "they", "them", "their", "what", "which", "who",
        "this", "that", "these", "those",
        // Auxiliary verbs
        "am", "is", "are", "was", "were", "be", "been", "being",
        "have", "has", "had", "do", "does", "did",
        "will", "would", "could", "should", "may", "might", "shall", "can",
        // Articles, prepositions, conjunctions
        "a", "an", "the", "and", "but", "or", "so",
        "if", "as", "at", "by", "for", "in", "of", "on", "to",
        "up", "with", "about", "out", "from", "into",
        "then", "than", "also", "just", "how",
        "all", "each", "both", "more", "when", "where",
        "there", "here", "now", "still", "such", "even",
        "get", "got", "go", "going", "one", "two", "some", "back"
    );

    // ── Improvement 2: Negation triggers ─────────────────────────────────────
    // When one of these appears, the next NEGATION_WINDOW words get a NOT_ prefix.
    // "not happy" → ["not", "NOT_happy"]
    // This is critical for mental health text: "I am not okay" ≠ "I am okay"
    private static final Set<String> NEGATION_TRIGGERS = Set.of(
        "not", "no", "never", "neither", "nobody", "nothing", "nowhere",
        // Contractions after removing apostrophes:
        "dont", "doesnt", "didnt", "wont", "cant",
        "couldnt", "shouldnt", "wouldnt",
        "isnt", "arent", "wasnt", "werent",
        "havent", "hasnt", "hadnt"
    );

    private static final int NEGATION_WINDOW = 2;

    // ── Model data structures ─────────────────────────────────────────────────
    private final Map<String, Integer>              documentCountByEmotion = new HashMap<>();
    private final Map<String, Map<String, Integer>> wordCountByEmotion     = new HashMap<>();
    private final Map<String, Integer>              totalWordsByEmotion    = new HashMap<>();
    private final Set<String>                       vocabulary             = new HashSet<>();

    private int totalDocuments = 0;

    private final List<String> emotions = List.of(
            "happiness", "sadness", "anxiety", "anger", "neutral"
    );

    // ── Training ──────────────────────────────────────────────────────────────
    @PostConstruct
    public void trainModel() {
        try {
            for (String emotion : emotions) {
                documentCountByEmotion.put(emotion, 0);
                wordCountByEmotion.put(emotion, new HashMap<>());
                totalWordsByEmotion.put(emotion, 0);
            }

            ClassPathResource resource = new ClassPathResource("dataset_emotions.csv");

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                boolean isFirstLine = true;

                while ((line = reader.readLine()) != null) {
                    if (isFirstLine) { isFirstLine = false; continue; }

                    String[] parts = splitCsvLine(line);
                    if (parts.length != 2) continue;

                    String emotion = parts[1].trim().toLowerCase();
                    if (!documentCountByEmotion.containsKey(emotion)) continue;

                    // Use the improved tokenizer (stop words + negation)
                    String[] tokens = tokenize(parts[0]);

                    totalDocuments++;
                    documentCountByEmotion.put(emotion, documentCountByEmotion.get(emotion) + 1);

                    for (String token : tokens) {
                        if (token.isBlank()) continue;
                        vocabulary.add(token);
                        Map<String, Integer> counts = wordCountByEmotion.get(emotion);
                        counts.put(token, counts.getOrDefault(token, 0) + 1);
                        totalWordsByEmotion.put(emotion, totalWordsByEmotion.get(emotion) + 1);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error training Naive Bayes model", e);
        }
    }

    // ── Classification ────────────────────────────────────────────────────────
    public EmotionDetectionResult classify(String inputText) {
        if (inputText == null || inputText.isBlank()) {
            return new EmotionDetectionResult("neutral", List.of("neutral"));
        }

        String[] tokens = tokenize(inputText);

        Map<String, Double> scores = new HashMap<>();

        for (String emotion : emotions) {
            double prior = priorProbability(emotion);
            if (prior <= 0) continue;

            double score = Math.log(prior);
            for (String token : tokens) {
                if (!token.isBlank()) {
                    score += Math.log(likelihood(token, emotion));
                }
            }
            scores.put(emotion, score);
        }

        List<Map.Entry<String, Double>> sorted = new ArrayList<>(scores.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        String topEmotion    = sorted.get(0).getKey();
        String secondEmotion = sorted.size() > 1 ? sorted.get(1).getKey() : "";

        // ── Softmax: convert log-scores to probabilities ──────────────────────
        // Subtract max score first for numerical stability (prevents overflow).
        double maxScore = sorted.get(0).getValue();
        double sumExp   = sorted.stream()
                                .mapToDouble(e -> Math.exp(e.getValue() - maxScore))
                                .sum();

        double topProb    = Math.exp(0) / sumExp;           // exp(max - max) = 1
        double secondProb = sorted.size() > 1
                ? Math.exp(sorted.get(1).getValue() - maxScore) / sumExp
                : 0.0;

        // "Mixed" only when no emotion clearly dominates (top < 55%)
        // AND both top emotions are not neutral.
        if (topProb < 0.55
                && !secondEmotion.isEmpty()
                && !topEmotion.equals("neutral")
                && !secondEmotion.equals("neutral")) {
            return new EmotionDetectionResult("mixed", List.of(topEmotion, secondEmotion));
        }

        return new EmotionDetectionResult(topEmotion, List.of(topEmotion));
    }

    // ── Tokenizer: clean → negation → stop word filter ───────────────────────
    private String[] tokenize(String text) {
        // Step 1: lowercase and remove punctuation (apostrophes too, so "don't" → "dont")
        String cleaned = text.toLowerCase()
                             .replaceAll("[^a-z\\s]", "")
                             .trim();

        String[] words = cleaned.split("\\s+");

        // Step 2: apply negation marking
        String[] negated = applyNegation(words);

        // Step 3: remove stop words — but keep NOT_ prefixed tokens
        return Arrays.stream(negated)
                     .filter(w -> !w.isBlank())
                     .filter(w -> w.startsWith("NOT_") || !STOP_WORDS.contains(w))
                     .toArray(String[]::new);
    }

    // ── Negation: marks the next NEGATION_WINDOW words after a trigger ────────
    // Example: ["i", "am", "not", "happy", "today"]
    //       →  ["i", "am", "not", "NOT_happy", "NOT_today"]
    // After stop word removal: ["NOT_happy"]
    private String[] applyNegation(String[] words) {
        String[] result      = new String[words.length];
        int      negateLeft  = 0;

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (NEGATION_TRIGGERS.contains(word)) {
                result[i]  = word;          // keep the trigger itself
                negateLeft = NEGATION_WINDOW;
            } else if (negateLeft > 0) {
                result[i] = "NOT_" + word;  // prefix emotional words after trigger
                negateLeft--;
            } else {
                result[i] = word;
            }
        }
        return result;
    }

    // ── Naive Bayes math ──────────────────────────────────────────────────────
    private double priorProbability(String emotion) {
        if (totalDocuments == 0) return 1e-9;
        return (double) documentCountByEmotion.getOrDefault(emotion, 0) / totalDocuments;
    }

    private double likelihood(String token, String emotion) {
        Map<String, Integer> wordMap   = wordCountByEmotion.get(emotion);
        int                  wordCount = wordMap.getOrDefault(token, 0);
        int                  total     = totalWordsByEmotion.getOrDefault(emotion, 0);
        // Laplace smoothing
        return (double) (wordCount + 1) / (total + vocabulary.size());
    }

    // ── CSV parser ────────────────────────────────────────────────────────────
    private String[] splitCsvLine(String line) {
        int lastComma = line.lastIndexOf(',');
        if (lastComma == -1) return new String[0];
        return new String[]{
            line.substring(0, lastComma).trim(),
            line.substring(lastComma + 1).trim()
        };
    }
}
