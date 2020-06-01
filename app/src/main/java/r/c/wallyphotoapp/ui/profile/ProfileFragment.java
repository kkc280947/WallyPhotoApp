package r.c.wallyphotoapp.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.base.BaseFragment;
import r.c.wallyphotoapp.databinding.FragmentProfileBinding;
import r.c.wallyphotoapp.repository.datamodels.usermodel.User;
import r.c.wallyphotoapp.ui.login.LoginActivity;
import r.c.wallyphotoapp.utils.CommonAppUtils;
import r.c.wallyphotoapp.utils.WallyConstants;

import static r.c.wallyphotoapp.utils.CommonAppUtils.getCurrentUserUID;
import static r.c.wallyphotoapp.utils.WallyConstants.FIREBASE_USER_PATH;

public class ProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;
    private DatabaseReference myRef;
    private ValueEventListener valueEventListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAppComponent().inject(this);
        init();
        getDataFromFireBase();
        setUpListeners();
    }

    private void init(){
        Glide.with(this)
                .load(WallyConstants.PROFILE_PIC_URL)
                .placeholder(R.drawable.ic_gallery)
                .into(binding.imageBanner);
    }

    private void setUpListeners(){
        binding.imageBackArrow.setOnClickListener(v -> goBack());
        binding.buttonSave.setOnClickListener(v -> saveUserToFirebase());
        binding.buttonSignOut.setOnClickListener(v -> signOut());
    }

    private void getDataFromFireBase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(FIREBASE_USER_PATH).child(getCurrentUserUID());
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                binding.textNameFromFirebase.setText(user.getName());
                binding.textPhoneFromFirebase.setText(user.getPhoneNumber());
                Toast.makeText(getContext(), getString(R.string.success_profile_update),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), getString(R.string.error_profile_update),
                        Toast.LENGTH_SHORT).show();
            }
        };
        myRef.addValueEventListener(valueEventListener);
    }

    private void saveUserToFirebase() {
        CommonAppUtils.hideKeyboard(getActivity());
        if(validateForm()){
             FirebaseDatabase database = FirebaseDatabase.getInstance();
             DatabaseReference myRef = database.getReference(FIREBASE_USER_PATH).child(getCurrentUserUID());
             User user = new User(binding.textNameFromFirebase.getText().toString(),
                     binding.textPhoneFromFirebase.getText().toString());
             myRef.setValue(user);
        }
    }

    private boolean validateForm() {
        if(binding.textNameFromFirebase.getText().toString().trim().isEmpty()){
            binding.textNameFromFirebase.setError(getString(R.string.error_enter_name));
            return false;
        }
        if (TextUtils.isEmpty(binding.textPhoneFromFirebase.getText().toString().trim())) {
            binding.textPhoneFromFirebase.setError(getString(R.string.error_phone_number_empty));
            return false;
        }
        if (binding.textPhoneFromFirebase.getText().toString().trim().length() != 10) {
            binding.textPhoneFromFirebase.setError(getString(R.string.error_text_number_not_valid));
            return false;
        }
        return true;
    }

    private void signOut(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        Activity activity = getActivity();
        if(activity != null){
            activity.startActivity(new Intent(getContext(), LoginActivity.class));
            activity.finishAffinity();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        myRef.removeEventListener(valueEventListener);
    }
}
