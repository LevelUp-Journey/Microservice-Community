package com.levelup.journey.platform.moderation.infrastructure.services;

import com.levelup.journey.platform.moderation.domain.model.valueobjects.ReportCategory;
import com.levelup.journey.platform.moderation.domain.services.ContentAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ContentAnalysisServiceImpl implements ContentAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContentAnalysisServiceImpl.class);
    
    @Value("${moderation.banned-words.file-path:moderation/banned-words.txt}")
    private String bannedWordsFilePath;
    
    // Thread-safe sets for different categories of banned words
    private final Set<String> hateSpeechWords = ConcurrentHashMap.newKeySet();
    private final Set<String> harassmentWords = ConcurrentHashMap.newKeySet();
    private final Set<String> violenceWords = ConcurrentHashMap.newKeySet();
    private final Set<String> spamWords = ConcurrentHashMap.newKeySet();
    private final Set<String> discriminationWords = ConcurrentHashMap.newKeySet();
    private final Set<String> generalBannedWords = ConcurrentHashMap.newKeySet();
    
    // Pattern para separar palabras
    private static final Pattern WORD_SEPARATOR = Pattern.compile("[\\s\\p{Punct}]+");
    
    @PostConstruct
    public void initializeBannedWords() {
        loadBannedWordsFromFile();
        logger.info("Content analysis service initialized with {} total banned words", 
                   getTotalBannedWordsCount());
    }
    
    @Override
    public List<String> findSuspiciousWords(String content) {
        if (content == null || content.trim().isEmpty()) {
            return List.of();
        }
        
        List<String> suspiciousWords = new ArrayList<>();
        List<String> words = extractWords(content);
        
        for (String word : words) {
            if (isWordBanned(word)) {
                suspiciousWords.add(word);
            }
        }
        
        return suspiciousWords;
    }
    
    @Override
    public boolean isContentAppropriate(String content) {
        return findSuspiciousWords(content).isEmpty();
    }
    
    @Override
    public ReportCategory categorizeContent(List<String> suspiciousWords) {
        if (suspiciousWords == null || suspiciousWords.isEmpty()) {
            return ReportCategory.OTHER;
        }
        
        // Priority categorization
        for (String word : suspiciousWords) {
            if (hateSpeechWords.contains(word.toLowerCase())) {
                return ReportCategory.HATE_SPEECH;
            }
            if (discriminationWords.contains(word.toLowerCase())) {
                return ReportCategory.DISCRIMINATION;
            }
            if (violenceWords.contains(word.toLowerCase())) {
                return ReportCategory.VIOLENCE;
            }
            if (harassmentWords.contains(word.toLowerCase())) {
                return ReportCategory.HARASSMENT;
            }
            if (spamWords.contains(word.toLowerCase())) {
                return ReportCategory.SPAM;
            }
        }
        
        return ReportCategory.INAPPROPRIATE;
    }
    
    @Override
    public List<String> analyzeContent(String title, String content) {
        List<String> allSuspiciousWords = new ArrayList<>();
        
        if (title != null && !title.trim().isEmpty()) {
            allSuspiciousWords.addAll(findSuspiciousWords(title));
        }
        
        if (content != null && !content.trim().isEmpty()) {
            allSuspiciousWords.addAll(findSuspiciousWords(content));
        }
        
        // Remove duplicates while preserving order
        return allSuspiciousWords.stream()
                .distinct()
                .collect(Collectors.toList());
    }
    
    private void loadBannedWordsFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource(bannedWordsFilePath);
            
            if (!resource.exists()) {
                logger.warn("Banned words file not found: {}. Using default words.", bannedWordsFilePath);
                loadDefaultBannedWords();
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                String line;
                String currentCategory = "general";
                
                while ((line = reader.readLine()) != null) {
                    line = line.trim().toLowerCase();
                    
                    // Skip empty lines and comments
                    if (line.isEmpty() || line.startsWith("#")) {
                        // Check if comment indicates category
                        if (line.contains("odio") || line.contains("hate")) {
                            currentCategory = "hate";
                        } else if (line.contains("agresivo") || line.contains("harassment")) {
                            currentCategory = "harassment";
                        } else if (line.contains("violencia") || line.contains("violence")) {
                            currentCategory = "violence";
                        } else if (line.contains("spam")) {
                            currentCategory = "spam";
                        } else if (line.contains("discriminatorio") || line.contains("discrimination")) {
                            currentCategory = "discrimination";
                        }
                        continue;
                    }
                    
                    // Add word to appropriate category
                    addWordToCategory(line, currentCategory);
                }
            }
            
            logger.info("Loaded banned words from file: {} (Categories: hate={}, harassment={}, violence={}, spam={}, discrimination={}, general={})", 
                       bannedWordsFilePath, hateSpeechWords.size(), harassmentWords.size(), 
                       violenceWords.size(), spamWords.size(), discriminationWords.size(), 
                       generalBannedWords.size());
            
        } catch (IOException e) {
            logger.error("Error loading banned words from file: {}", bannedWordsFilePath, e);
            loadDefaultBannedWords();
        }
    }
    
    private void addWordToCategory(String word, String category) {
        switch (category) {
            case "hate" -> hateSpeechWords.add(word);
            case "harassment" -> harassmentWords.add(word);
            case "violence" -> violenceWords.add(word);
            case "spam" -> spamWords.add(word);
            case "discrimination" -> discriminationWords.add(word);
            default -> generalBannedWords.add(word);
        }
    }
    
    private void loadDefaultBannedWords() {
        // Default banned words if file loading fails
        Set<String> defaultWords = Set.of(
            "idiota", "estúpido", "imbécil", "tonto", "basura", 
            "inútil", "perdedor", "odio", "maldito"
        );
        
        generalBannedWords.addAll(defaultWords);
        logger.info("Loaded {} default banned words", defaultWords.size());
    }
    
    private List<String> extractWords(String content) {
        return Arrays.stream(WORD_SEPARATOR.split(content.toLowerCase()))
                .map(String::trim)
                .filter(word -> !word.isEmpty())
                .filter(word -> word.length() > 1) // Ignore single letters
                .collect(Collectors.toList());
    }
    
    private boolean isWordBanned(String word) {
        String lowerWord = word.toLowerCase();
        return hateSpeechWords.contains(lowerWord) ||
               harassmentWords.contains(lowerWord) ||
               violenceWords.contains(lowerWord) ||
               spamWords.contains(lowerWord) ||
               discriminationWords.contains(lowerWord) ||
               generalBannedWords.contains(lowerWord);
    }
    
    private int getTotalBannedWordsCount() {
        return hateSpeechWords.size() + harassmentWords.size() + violenceWords.size() + 
               spamWords.size() + discriminationWords.size() + generalBannedWords.size();
    }
}