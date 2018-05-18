package com.example.punit.stockacer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RC_SIGN_IN = 23;


    private GoogleSignInClient mGoogleSignInClient;
    String email_id;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp=getSharedPreferences("usrdata.txt",MODE_PRIVATE);
        name=sp.getString("username","");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        if(!name.isEmpty()) {
            Toast.makeText(this, "Hello " + name, Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this,Home.class).putExtra("user",name));
        }
        else
            Toast.makeText(this," Need to sign in first",Toast.LENGTH_LONG).show();


        findViewById(R.id.sign_in_button).setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        //updateUI(currentUser);
    }
    @Override
    public void onClick(View v){

                signIn();



        }
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            email_id=account.getEmail();
            name=email_id;

            // Signed in successfully, show authenticated UI.
            Toast.makeText(this,email_id+" Signed in successfully",Toast.LENGTH_LONG).show();
            SharedPreferences sp=getSharedPreferences("usrdata.txt",MODE_PRIVATE);
            SharedPreferences.Editor edit=sp.edit();
            edit.putString("username",name);
            edit.commit();
            startActivity(new Intent(MainActivity.this,Home.class).putExtra("user",name));
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this,"Sign in failed "+e.getStatusCode(),Toast.LENGTH_LONG).show();

        }
    }

}
