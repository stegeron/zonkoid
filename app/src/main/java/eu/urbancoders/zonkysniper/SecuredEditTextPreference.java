package eu.urbancoders.zonkysniper;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import eu.urbancoders.zonkysniper.core.SecurityManager;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 05.06.2016
 */
public class SecuredEditTextPreference extends EditTextPreference {

    public SecuredEditTextPreference(Context context) {
        super(context);
    }

    public SecuredEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SecuredEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.setText(restoreValue ? getPersistedString(null) : (String) defaultValue);
    }

    @Override
    public void setText(String text) {
        if (text == null || text.length() == 0) super.setText(text);
        super.setText(SecurityManager.getInstance(getContext())
                .encryptString(text));
    }

    @Override
    public String getText() {
        String text = super.getText();
        if (text == null || text.length() == 0) return text;
        return SecurityManager.getInstance(getContext())
                .decryptString(text);
    }
}
