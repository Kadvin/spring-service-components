/**
 * Developer: Kadvin Date: 14-5-16 上午9:10
 */
package dnt.spring;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import java.util.Locale;

/**
 * The bean support translate
 */
public class TranslateSupportBean extends Bean implements MessageSourceAware {

    private MessageSource messageSource;
    private Locale locale =  Locale.getDefault();

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    protected String translate(String code, Object... args){
        return messageSource.getMessage(code, args, locale);
    }
}
