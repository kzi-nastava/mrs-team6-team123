package com.example.mobile_application.ui;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.example.mobile_application.R;
import com.example.mobile_application.dto.ChangePasswordRequestDTO;
import com.example.mobile_application.repository.UserProfileRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Helper class for managing password change dialog
 */
public class PasswordChangeDialogHelper {
    private static final long DEFAULT_USER_ID = 3L; // Default user ID
    private static final String TAG = "PasswordChangeDialog";

    public static void showChangePasswordDialog(Context context) {
        showChangePasswordDialog(context, DEFAULT_USER_ID);
    }

    public static void showChangePasswordDialog(Context context, Long userId) {
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

        // Create the dialog
        AlertDialog[] dialog = new AlertDialog[1];
        dialog[0] = new AlertDialog.Builder(context)
                .setTitle("Change password")
                .setView(dialogView)
                .setPositiveButton("Save", null) // Set to null, will override onClick
                .setNegativeButton("Cancel", null)
                .show();

        dialog[0].getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String currentPassword = oldPass.getText().toString().trim();
            String newPassword = newPass.getText().toString().trim();
            String confirmPassword = confirmPass.getText().toString().trim();

            // Validation
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(context, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button to prevent multiple clicks
            dialog[0].getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            // Call backend
            UserProfileRepository repository = new UserProfileRepository();
            ChangePasswordRequestDTO request = new ChangePasswordRequestDTO(currentPassword, newPassword);

            repository.changePassword(userId, request, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        dialog[0].dismiss();
                    } else {
                        Toast.makeText(context, "Failed to change password", Toast.LENGTH_SHORT).show();
                        dialog[0].getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Password change request failed", t);
                    String errorMsg = "Error: " + (t.getMessage() != null ? t.getMessage() : "Unknown error");
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    dialog[0].getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            });
        });
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
