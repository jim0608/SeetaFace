package com.mysafe.lib_base.custom_control;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.mysafe.lib_base.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditText_SpecialCharacterBanded extends AppCompatEditText {

    private static final int CHAR_MAX_LENGTH = 14;

    public EditText_SpecialCharacterBanded(@NonNull Context context) {
        super(context);
    }

    public EditText_SpecialCharacterBanded(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Initialize();
    }

    public EditText_SpecialCharacterBanded(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initialize();
    }

    private void Initialize() {
        this.setMaxEms(CHAR_MAX_LENGTH);
        this.setMaxLines(1);
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(CHAR_MAX_LENGTH), new SpecialCharacterFiler()});
    }

    private class SpecialCharacterFiler implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.toString().equals("\"") || source.toString().equals(" "))
                return "";
            String speChar = getContext().getString(R.string.str_special_character);
            Pattern pattern = Pattern.compile(speChar);
            Matcher matcher = pattern.matcher(source.toString());
            if (matcher.find()) return "";
            else return null;
        }
    }
}