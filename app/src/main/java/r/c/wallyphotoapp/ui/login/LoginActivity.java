package r.c.wallyphotoapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.WallyApplication;
import r.c.wallyphotoapp.base.BaseActivity;
import r.c.wallyphotoapp.repository.datamodels.usermodel.User;
import r.c.wallyphotoapp.ui.otp.OTPFragment;
import r.c.wallyphotoapp.ui.photogallery.PhotoGalleryActivity;

import static r.c.wallyphotoapp.utils.WallyConstants.FIREBASE_USER_PATH;

/**
 * LoginActivity opens at first. If firebase user is already signed in then navigate to
 * PhotoGalleryFragment otherwise navigates to phone login screen.
 * */
public class LoginActivity extends BaseActivity implements IActivityCallback {

    private static final String TAG = "PhoneAuthActivity";

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ((WallyApplication)getApplication()).getAppComponent().inject(this);
        initFirebase();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            moveToMain();
        }else {
            moveToLoginFragment();
        }
    }

    @Override
    public void verifyPhoneNumberWithCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void phoneAutoVerified(PhoneAuthCredential credential) {
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void moveToOTPWithID(String verificationId, String mobileNumber) {
        this.mVerificationId = verificationId;
        this.phoneNumber = mobileNumber;
        getSupportFragmentManager().beginTransaction()
                .replace(getContainerViewId(), OTPFragment.newInstance(),OTPFragment.class.getName())
                .addToBackStack("otp")
                .commit();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        AuthResult authResult  = task.getResult();
                        if(authResult != null){
                            FirebaseUser user = task.getResult().getUser();
                            if(user != null){
                                saveUserToFirebase(user.getUid());
                            }
                        }
                        OTPFragment fragment = (OTPFragment) getSupportFragmentManager()
                                .findFragmentByTag(OTPFragment.class.getName());
                        if (fragment != null) {
                            if (credential.getSmsCode() != null) {
                                fragment.setOTP(credential.getSmsCode());
                            }
                        }
                        moveToMain();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            OTPFragment fragment = (OTPFragment) getSupportFragmentManager()
                                    .findFragmentByTag(OTPFragment.class.getName());
                            if(fragment!=null){
                                fragment.setInvalidOTPError();
                            }
                        }
                    }
                });
    }

    private void saveUserToFirebase(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FIREBASE_USER_PATH).child(uid).child("phoneNumber");
        myRef.setValue(phoneNumber);
    }


    @Override
    public int getContainerViewId() {
        return R.id.frameContainer;
    }

    private void moveToMain() {
        startActivity(new Intent(this, PhotoGalleryActivity.class));
        finishAffinity();
    }

    public void moveToLoginFragment(){
        getSupportFragmentManager().beginTransaction()
                .add(getContainerViewId(), new LoginFragment(),LoginFragment.TAG)
                .commit();
    }
}
