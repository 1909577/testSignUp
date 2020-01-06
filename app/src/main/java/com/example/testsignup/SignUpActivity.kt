package com.example.testsignup

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.emailTB
import kotlinx.android.synthetic.main.activity_sign_up.passwordTB

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        registerBtn.setOnClickListener {
            createAccount()
        }


    }

    private fun createAccount() {

        val username=usernameTB.text.toString()
        val email=emailTB.text.toString()
        val password=passwordTB.text.toString()

        if(usernameTB.text.toString().isEmpty()){
            usernameTB.error="Please enter username"
            usernameTB.requestFocus()
        }else if(emailTB.text.toString().isEmpty()){
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
            progressDialog.setTitle("SignUp")
            progressDialog.setMessage("Please wait, this may take a while...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        saveUserInfo(username,email, password,progressDialog)
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        progressDialog.dismiss()
                    }
                }
        }

    }

    private fun saveUserInfo(username: String, email: String, password: String, progressDialog: ProgressDialog) {
        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap=HashMap<String,Any>()
        userMap["uid"]=currentUserID
        userMap["username"]=username
        userMap["email"]=email
        userMap["password"]=password
        userMap["score"]=0
        userMap["image"]="https://firebasestorage.googleapis.com/v0/b/testass-7a969.appspot.com/o/Default%20images%2Fprofile.jpg?alt=media&token=8f377486-f2b5-41f2-a463-62758ea11f42"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Account has been created successfully.", Toast.LENGTH_LONG)
                    val user=FirebaseAuth.getInstance().currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener{task ->
                            if(task.isSuccessful){
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(this,MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
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
