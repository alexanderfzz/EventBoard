package Scraping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
public class Toolbox {
    public static String ObjectToJSON(ObjectMapper objectMapper, Object target) throws JsonProcessingException {
        return objectMapper.writeValueAsString(target);
    }
}
