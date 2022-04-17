package eu.deysouvik.sign_upsign_intofirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class MainActivity : AppCompatActivity() {

   lateinit var mauth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mauth= FirebaseAuth.getInstance()

        if(mauth.currentUser!=null){
            val intent=Intent(this,Welcome::class.java)
            startActivity(intent)
            finish()
        }

        sign_in.setOnClickListener {
            if(et_email.text.isEmpty()||et_password.text.isEmpty()){
                Toast.makeText(this, "Email or Password is not filled", Toast.LENGTH_SHORT).show()
            }
            else{
                mauth.signInWithEmailAndPassword(et_email.text.toString(),et_password.text.toString()).addOnCompleteListener(this){ task->
                   if(task.isSuccessful){
                       Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                       val intent=Intent(this,Welcome::class.java)
                       startActivity(intent)
                       finish()
                   }
                   else{
                       Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                   }
                }
            }

        }

        sign_up.setOnClickListener {
            val i=Intent(this,Sign_In::class.java)
            startActivity(i)

        }

    }
}