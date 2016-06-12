package com.pratamawijaya.firebasereborn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginViewActivity extends AppCompatActivity
    implements GoogleApiClient.OnConnectionFailedListener {

  private static final int REQCODE_SIGN_IN = 0;

  private GoogleSignInOptions googleSignInOptions;
  private GoogleApiClient googleApiClient;
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener authStateListener;

  private SignInButton signInButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_view);

    firebaseAuth = FirebaseAuth.getInstance();
    authStateListener = new FirebaseAuth.AuthStateListener() {

      @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
          // user has signed
          Log.d("debug", "user name " + user.getDisplayName());
          startActivity(new Intent(LoginViewActivity.this, HomeViewActivity.class));
        } else {
          // user has logout
        }
      }
    };

    setupGoogleApi();

    signInButton = (SignInButton) findViewById(R.id.btn_login);

    signInButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intentSignIn = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intentSignIn, REQCODE_SIGN_IN);
      }
    });
  }

  @Override protected void onStart() {
    super.onStart();
    firebaseAuth.addAuthStateListener(authStateListener);
  }

  @Override protected void onStop() {
    super.onStop();
    if (authStateListener != null) {
      firebaseAuth.removeAuthStateListener(authStateListener);
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQCODE_SIGN_IN && resultCode == RESULT_OK) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

      if (result.isSuccess()) {
        // user success login, get account data
        GoogleSignInAccount account = result.getSignInAccount();

        loginWithFirebase(account);
      } else {
        // user failed login
        // TODO: 6/12/16 do something
      }
    }
  }

  private void loginWithFirebase(GoogleSignInAccount account) {
    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
              // if login has not successfull
              // TODO: 6/12/16 do something
            }
          }
        });
  }

  private void setupGoogleApi() {
    googleSignInOptions =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            getString(R.string.default_web_client_id)).requestEmail().build();

    googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
        .build();
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    // TODO: 6/12/16 do something if failed
  }
}
