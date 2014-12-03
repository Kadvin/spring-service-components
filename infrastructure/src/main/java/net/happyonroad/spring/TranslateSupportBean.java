/**
 * Developer: Kadvin Date: 14-5-16 上午9:10
 */
package net.happyonroad.spring;

import net.happyonroad.util.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * The bean support translate
 */
public class TranslateSupportBean extends Bean implements MessageSourceAware{
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected String translate(String code, Object... args){
        return StringUtils.translate(messageSource, code, args);
    }
}
