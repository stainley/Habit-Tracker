package ca.lambton.habittracker.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.InputMethodService;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.io.InputStream;

import ca.lambton.habittracker.R;

public class ImageInputMethodService extends InputMethodService {
    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.input_view, null);

        return view;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(() -> {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                ClipData.Item item = clipData.getItemAt(0);
                if (item.getUri() != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(item.getUri());
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(" ");
                        builder.setSpan(new ImageSpan(getApplicationContext(), bitmap), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        EditText editText = getCurrentInputView().findViewById(R.id.edit_text);
                        editText.getText().insert(editText.getSelectionStart(), builder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private View getCurrentInputView() {
        return null;
    }
}
