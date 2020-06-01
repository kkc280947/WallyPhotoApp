package r.c.wallyphotoapp.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.base.BaseCallbackFragment;
import r.c.wallyphotoapp.databinding.FragmentLoginBinding;
import r.c.wallyphotoapp.utils.CommonAppUtils;

public class LoginFragment extends BaseCallbackFragment<LoginViewModel,IActivityCallback> implements View.OnClickListener {

    public FragmentLoginBinding binding;
    static final String TAG = LoginFragment.class.getName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActivityCallback(IActivityCallback.class);
        binding.buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_submit) {
            CommonAppUtils.hideKeyboard(getActivity());
            if (!validatePhoneNumber()) {
                return;
            }
            startPhoneNumberVerification(Objects.requireNonNull(binding.editTextMobile.getText()).toString());
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = Objects.requireNonNull(binding.editTextMobile.getText()).toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            binding.editTextMobile.setError(getString(R.string.error_phone_number_empty));
            return false;
        }
        if (phoneNumber.length() != 10) {
            binding.editTextMobile.setError(getString(R.string.error_text_number_not_valid));
            return false;
        }
        return true;
    }

    private void showProgressBar(boolean show){
        if(show){
            binding.buttonSubmit.setVisibility(View.INVISIBLE);
            binding.progressPhone.setVisibility(View.VISIBLE);
        }else {
            binding.buttonSubmit.setVisibility(View.VISIBLE);
            binding.progressPhone.setVisibility(View.INVISIBLE);
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        Activity activity = getActivity();
        if(activity!=null){
            showProgressBar(true);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91"+phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    activity,
                    mCallbacks);
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NotNull PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted:" + credential);
            getActivityCallback().phoneAutoVerified(credential);
            showProgressBar(false);
        }

        @Override
        public void onVerificationFailed(@NotNull FirebaseException e) {
            Log.w(TAG, "onVerificationFailed", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                binding.editTextMobile.setError(getString(R.string.error_invalid_number));
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getContext(), getString(R.string.error_quota_exceeded),
                        Toast.LENGTH_SHORT).show();
            }
            showProgressBar(false);
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent:" + verificationId);
            getActivityCallback().moveToOTPWithID(verificationId, binding.editTextMobile.getText().toString().trim());
            showProgressBar(false);
        }
    };
}
