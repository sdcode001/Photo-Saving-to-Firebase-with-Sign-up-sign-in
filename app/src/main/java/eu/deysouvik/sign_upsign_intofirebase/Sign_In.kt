package eu.deysouvik.sign_upsign_intofirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*

class Sign_In : AppCompatActivity() {

    lateinit var db:FirebaseDatabase
    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        db= FirebaseDatabase.getInstance("https://sign-in-sign-up-to-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/")
        auth=FirebaseAuth.getInstance()


        Sign_Up.setOnClickListener {
            if(et_Email.text.isEmpty()||et_Password.text.isEmpty()||et_Name.text.isEmpty()){
                Toast.makeText(this, "Name or Email or Password is not filled", Toast.LENGTH_SHORT).show()
            }
            else{
                auth.createUserWithEmailAndPassword(et_Email.text.toString(),et_Password.text.toString()).addOnCompleteListener(this){ task->

                    if(task.isSuccessful){
                        val user=User(et_Name.text.toString(),et_Email.text.toString(),et_Password.text.toString())
                        val id= task.result.user!!.uid
                        db.reference.child("Users").child(id).setValue(user)
                        Toast.makeText(this, "User Sign up successfully", Toast.LENGTH_SHORT).show()
                        val i= Intent(this,MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                    else{
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        val i= Intent(this,MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }

        }

    }
}