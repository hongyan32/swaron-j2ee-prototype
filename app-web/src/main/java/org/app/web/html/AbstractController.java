package org.app.web.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class AbstractController.
 */
public abstract class AbstractController extends ApplicationObjectSupport {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Gets the i18n message.
     * 
     * @param code the code
     * @return the message
     */
    public String getMessage(String code) {
        return super.getMessageSourceAccessor().getMessage(code);
    }

    protected List<String> getErrorMessages(final Errors errors) {
        List<String> errorMessage = new ArrayList<String>();
        List<ObjectError> list = errors.getAllErrors();
        for (ObjectError objectError : list) {
            errorMessage.add(getMessageSourceAccessor().getMessage(objectError));
        }
        return errorMessage;
    }

    public String objectToJson(Object object) {
        try {
            String jsonValue = objectMapper.writeValueAsString(object);
            return StringUtils.replace(jsonValue, "</script>", "<\\/script>");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    
    public <T> T jsonToObject(String jsonValue, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonValue, valueType);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
    
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
