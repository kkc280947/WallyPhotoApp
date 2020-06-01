package r.c.wallyphotoapp.ui.otp;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.base.BaseCallbackFragment;
import r.c.wallyphotoapp.databinding.FragmentOtpBinding;
import r.c.wallyphotoapp.ui.login.IActivityCallback;
import r.c.wallyphotoapp.utils.CommonAppUtils;

import static r.c.wallyphotoapp.utils.CommonAppUtils.isNetworkConnected;
import static r.c.wallyphotoapp.utils.CommonAppUtils.showInternetError;

public class OTPFragment extends BaseCallbackFragment<OTPViewModel, IActivityCallback> {

    private FragmentOtpBinding binding;

    public static OTPFragment newInstance(){
        return new OTPFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActivityCallback(IActivityCallback.class);
        binding.buttonSubmitOtp.setOnClickListener(v -> {
            submitOTP();
        });
    }

    private void submitOTP() {
        CommonAppUtils.hideKeyboard(getActivity());
        String code = binding.editTextOtp.getText().toString();
        if (TextUtils.isEmpty(code)) {
            binding.editTextOtp.setError(getString(R.string.error_empty_otp));
            return;
        }
        showProgressBar(true);
        Context context = getContext();
        if(context!=null){
            if(isNetworkConnected(context)){
                getActivityCallback().verifyPhoneNumberWithCode(code);
            }else {
                showInternetError(context,(dialog, which) -> dialog.dismiss());
            }
        }
    }

    private void showProgressBar(boolean show){
        if(show){
            binding.buttonSubmitOtp.setVisibility(View.INVISIBLE);
            binding.progressOTP.setVisibility(View.VISIBLE);
        }else {
            binding.buttonSubmitOtp.setVisibility(View.VISIBLE);
            binding.progressOTP.setVisibility(View.INVISIBLE);
        }
    }

    public void setInvalidOTPError(){
        binding.editTextOtp.setError(getString(R.string.text_invalid_otp));
        showProgressBar(false);
    }

    public void setOTP(String code){
        binding.editTextOtp.setText(code);
    }
}
