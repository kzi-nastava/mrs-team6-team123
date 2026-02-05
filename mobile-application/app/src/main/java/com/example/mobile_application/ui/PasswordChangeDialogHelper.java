package com.example.mobile_application.ui;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.example.mobile_application.R;

/**
 * Helper class for managing password change dialog
 */
public class PasswordChangeDialogHelper {

    public static void showChangePasswordDialog(Context context) {
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_change_password, null);

        // Initialize password input fields
        EditText oldPass = dialogView.findViewById(R.id.edit_old_password);
        EditText newPass = dialogView.findViewById(R.id.edit_new_password);
        EditText confirmPass = dialogView.findViewById(R.id.edit_confirm_password);

        // Initialize visibility toggle buttons
        ImageView toggleOldPass = dialogView.findViewById(R.id.toggle_old_password);
        ImageView toggleNewPass = dialogView.findViewById(R.id.toggle_new_password);
        ImageView toggleConfirmPass = dialogView.findViewById(R.id.toggle_confirm_password);

        // Setup toggle listeners for showing/hiding passwords
        toggleOldPass.setOnClickListener(v -> togglePasswordVisibility(oldPass));
        toggleNewPass.setOnClickListener(v -> togglePasswordVisibility(newPass));
        toggleConfirmPass.setOnClickListener(v -> togglePasswordVisibility(confirmPass));

        // Create and show the dialog
        new AlertDialog.Builder(context)
                .setTitle("Change password")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // todo : Implement password validation
                    Toast.makeText(context, "Password changed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static void togglePasswordVisibility(EditText editText) {
        int currentType = editText.getInputType();

        // Toggle between visible and hidden password input types
        if (currentType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        // Move cursor to end of text after input type change
        editText.setSelection(editText.getText().length());
    }
}
