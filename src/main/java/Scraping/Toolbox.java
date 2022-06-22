package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

public class Toolbox {
    public static String ObjectToJSON(ObjectMapper objectMapper, Object target) throws JsonProcessingException {
        return objectMapper.writeValueAsString(target);
    }

    public static boolean isNotDuplicate(Set<String> existingSet, String target) {
        return existingSet.add(target);
    }
}
