package com.example.aggregator.service;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.example.aggregator.client.AggregatorRestClient;
import com.example.aggregator.model.Entry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AggregatorService {

    private final AggregatorRestClient aggregatorRestClient;

    public AggregatorService(AggregatorRestClient aggregatorRestClient) {
        this.aggregatorRestClient = aggregatorRestClient;
    }

    public Entry getDefinitionFor(String word) {
        return aggregatorRestClient.getDefinitionFor(word);
    }

    public List<Entry> getWordsThatContainSuccessiveLettersAndStartsWith(String chars) {

        List<Entry> wordsThatStartWith = aggregatorRestClient.getWordsStartingWith(chars);
        List<Entry> wordsThatContainSuccessiveLetters = aggregatorRestClient.getWordsThatContainConsecutiveLetters();

        // stream API version
        List<Entry> common = wordsThatStartWith.stream()
                .filter(wordsThatContainSuccessiveLetters::contains)
                .toList();

        /*List<Entry> common = new ArrayList<>(wordsThatStartWith);
        common.retainAll(wordsThatContainSuccessiveLetters);*/

        return common;

    }

    public List<Entry> getWordsThatContain(String chars) {

        return aggregatorRestClient.getWordsThatContain(chars);

    }



    public List<Entry> getAllPalindromes() {
        List<Entry> palindromes = new ArrayList<>();

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        for (char c : alphabet) {
            String letter = Character.toString(c);

            List<Entry> startsWith = aggregatorRestClient.getWordsStartingWith(letter);
            List<Entry> endsWith = aggregatorRestClient.getWordsEndingWith(letter);

            for (Entry startEntry : startsWith) {
                for (Entry endEntry : endsWith) {
                    if (startEntry.getWord().equalsIgnoreCase(endEntry.getWord())) {

                        String word = startEntry.getWord();
                        String reversed = reverse(word);

                        if (word.equalsIgnoreCase(reversed)) {
                            palindromes.add(startEntry);
                            break;
                        }
                    }
                }
            }
        }

        // Sort the result alphabetically (case-insensitive)
        for (int i = 0; i < palindromes.size() - 1; i++) {
            for (int j = i + 1; j < palindromes.size(); j++) {
                String word1 = palindromes.get(i).getWord();
                String word2 = palindromes.get(j).getWord();
                if (word1.compareToIgnoreCase(word2) > 0) {
                    Entry temp = palindromes.get(i);
                    palindromes.set(i, palindromes.get(j));
                    palindromes.set(j, temp);
                }
            }
        }

        return palindromes;
    }

    // Helper method to reverse a word
    private String reverse(String word) {
        char[] chars = word.toCharArray();
        int left = 0;
        int right = chars.length - 1;

        while (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }

        return new String(chars);
    }


}
