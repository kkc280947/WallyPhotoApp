package r.c.wallyphotoapp.ui.login;

import com.google.firebase.auth.PhoneAuthCredential;

public interface IActivityCallback {
    void verifyPhoneNumberWithCode(String code);
    void phoneAutoVerified(PhoneAuthCredential credential);
    void moveToOTPWithID(String verificationId, String mobileNumber);
}
