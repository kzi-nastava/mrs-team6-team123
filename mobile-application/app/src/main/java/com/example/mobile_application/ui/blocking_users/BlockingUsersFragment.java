package com.example.mobile_application.ui.blocking_users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mobile_application.R;
import com.example.mobile_application.dto.UserDTO;
import com.example.mobile_application.repository.BlockUserRepository;
import com.example.mobile_application.repository.UserRepository;
import java.util.List;

import com.example.mobile_application.service.ApiClient;
import com.example.mobile_application.service.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockingUsersFragment extends Fragment {
    private LinearLayout usersListLayout;
    private BlockUserRepository blockUserRepository;
    private UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blocking_users, container, false);
        usersListLayout = view.findViewById(R.id.users_list_layout);
        blockUserRepository = new BlockUserRepository();
        userRepository = new UserRepository();
        loadUsers();
        return view;
    }

    private Long getCurrentUserId() {
        TokenManager tokenManager = ApiClient.getTokenManager();
        return tokenManager.getUserId();
    }

    private void loadUsers() {
        Long currentUserId = getCurrentUserId();
        userRepository.getAllActivatedUsers(currentUserId, new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usersListLayout.removeAllViews();
                    for (UserDTO user : response.body()) {
                        View userRow = LayoutInflater.from(getContext()).inflate(R.layout.item_user_block,
                                usersListLayout, false);
                        TextView userInfo = userRow.findViewById(R.id.user_info);
                        Button blockBtn = userRow.findViewById(R.id.block_btn);
                        userInfo.setText(user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
                        if (user.isAccountBlocked()) {
                            blockBtn.setText("Unblock");
                            blockBtn.setBackgroundResource(R.drawable.btn_unblock);
                        } else {
                            blockBtn.setText("Block");
                            blockBtn.setBackgroundResource(R.drawable.btn_block);
                        }
                        blockBtn.setOnClickListener(v -> toggleBlockUser(user, blockBtn));
                        usersListLayout.addView(userRow);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                // Optionally show error
            }
        });
    }

    private void toggleBlockUser(UserDTO user, Button btn) {
        if (user.isAccountBlocked()) {
            userRepository.unblockUser(user.getId(), new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    user.setAccountBlocked(false);
                    btn.setText("Block");
                    btn.setBackgroundResource(R.drawable.btn_block);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Optionally show error
                }
            });
        } else {
            userRepository.blockUser(user.getId(), new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    user.setAccountBlocked(true);
                    btn.setText("Unblock");
                    btn.setBackgroundResource(R.drawable.btn_unblock);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Optionally show error
                }
            });
        }
    }
}
