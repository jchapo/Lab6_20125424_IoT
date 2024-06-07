package com.example.lab6_20125424_iot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab6_20125424_iot.dataHolder.DataManager;
import com.example.lab6_20125424_iot.item.ListElementEgreso;
import com.example.lab6_20125424_iot.item.ListElementIngreso;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton, googleSignInButton;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Log.w("LoginActivity", "Google sign in failed", e);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Usa el layout adecuado

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);

        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> startSignIn());
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        // Configura Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Verifica si el usuario ya ha iniciado sesión
        if (mAuth.getCurrentUser() != null) {
            updateUI(mAuth.getCurrentUser());
        }
    }

    private void startSignIn() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese un usuario y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            DataManager.getInstance().setUserId(user.getUid());
                        }
                        updateUI(user);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        startActivity(intent);
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            DataManager.getInstance().setUserId(user.getUid());
                        }
                        updateUI(user);
                    } else {
                        Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Log.d("msg-test", "Firebase uid: " + user.getUid());
                DataManager.getInstance().setUserId(user.getUid());
                updateUI(user);
            }
        } else {
            Log.d("msg-test", "Canceló el Log-in");
            updateUI(null);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            loadIngresosFromFirestore(() -> {
                loadEgresosFromFirestore(() -> {
                    Intent intent = new Intent(LoginActivity.this, Navegation.class);
                    startActivity(intent);
                    finish();
                });
            });
        } else {
            Toast.makeText(this, "Sesión cancelada", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadIngresosFromFirestore(Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        String path = "users/" + uid + "/ingresos";
        db.collection(path)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListElementIngreso> ingresosList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListElementIngreso ingreso = document.toObject(ListElementIngreso.class);
                            ingreso.setId(document.getId());
                            ingresosList.add(ingreso);
                        }
                        DataManager.getInstance().setIngresosList(ingresosList);

                        // Log the elements of ingresosList
                        for (ListElementIngreso ingreso : ingresosList) {
                            Log.d("msg-test", "Ingreso: " + ingreso.getAmount());
                        }

                        onSuccess.run();
                    } else {
                        Log.d("msg-test", "Error getting ingreso documents: ", task.getException());
                    }
                });
    }

    public void loadEgresosFromFirestore(Runnable onSuccess) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        String path = "users/" + uid + "/egresos";
        db.collection(path)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ListElementEgreso> egresosList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ListElementEgreso egreso = document.toObject(ListElementEgreso.class);
                            egreso.setId(document.getId());
                            egresosList.add(egreso);
                        }
                        DataManager.getInstance().setEgresosList(egresosList);

                        // Log the elements of egresosList
                        for (ListElementEgreso egreso : egresosList) {
                            Log.d("msg-test", "Egreso: " + egreso.getAmount());
                        }

                        onSuccess.run();
                    } else {
                        Log.d("msg-test", "Error getting egreso documents: ", task.getException());
                    }
                });
    }

}
