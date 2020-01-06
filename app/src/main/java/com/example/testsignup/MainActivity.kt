package com.example.testsignup

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        signUpBtn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        loginBtn.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email=emailTB.text.toString()
        val password=passwordTB.text.toString()

        if(emailTB.text.toString().isEmpty()){
            emailTB.error="Please enter email"
            emailTB.requestFocus()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailTB.text.toString()).matches()){
            emailTB.error="Please enter correct format eg.xx@xxxx.com"
            emailTB.requestFocus()
        }else if(passwordTB.text.toString().isEmpty()){
            passwordTB.error="Please enter password"
            passwordTB.requestFocus()
        }else{
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Login")
            progressDialog.setMessage("Please wait, this may take a while...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task->
                if(task.isSuccessful){
                    if(mAuth.currentUser!!.isEmailVerified){
                        progressDialog.dismiss()
                        val intent = Intent(this, SignInActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }else{
                        progressDialog.dismiss()
                        Toast.makeText(
                        baseContext,"Please verify your email address.",
                        Toast.LENGTH_LONG
                        ).show()
                    }


                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser=FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){
                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

        }
    }


}
