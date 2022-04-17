package eu.deysouvik.sign_upsign_intofirebase

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_welcome.*
import java.text.SimpleDateFormat
import java.util.*

class Welcome : AppCompatActivity() {
    lateinit var Auth:FirebaseAuth
    companion object{
        const val GALLERY=100
        const val IMAGE_NAME="img_name"
        const val SP_NAME="MSP"
    }
    lateinit var fDatabase:FirebaseDatabase
    lateinit var fStorage:FirebaseStorage
    lateinit var ref:StorageReference
    lateinit var cal: Calendar
    lateinit var mSharedPref:SharedPreferences
    lateinit var iv:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        mSharedPref=getSharedPreferences(SP_NAME, MODE_PRIVATE)
        iv=findViewById(R.id.iv)
        Auth= FirebaseAuth.getInstance()
        fDatabase= FirebaseDatabase.getInstance("https://sign-in-sign-up-to-firebase-default-rtdb.asia-southeast1.firebasedatabase.app/")
        fStorage= FirebaseStorage.getInstance()
        cal= Calendar.getInstance()
        val img_name=mSharedPref.getString(IMAGE_NAME,"")
        //read data from database
        fDatabase.getReference("/Images/$img_name").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 val img_link=snapshot.value.toString()
                 Picasso.get().load(img_link).into(iv)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        log_out.setOnClickListener {
            Auth.signOut()
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }



        photo.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== GALLERY && data!=null){
            val uri: Uri? =data.data
            iv.setImageURI(uri)
            val fStorageRef=fStorage.getReference()
            val Time=cal.time
            val sdf=SimpleDateFormat("hh:mm:ss",Locale.getDefault())
            val time=sdf.format(Time)
            val currTime=time
            val editor=mSharedPref.edit()
            editor.putString(IMAGE_NAME,currTime)
            editor.apply()
            fStorageRef.child("images").child(currTime).putFile(uri!!).addOnCompleteListener{ Task->
               if(Task.isSuccessful){
                   ref=fStorage.getReference("/images/$currTime")
                   ref.downloadUrl.addOnCompleteListener {  task->

                       if(task.isSuccessful){
                           val downloadUrl:Uri=task.result

                           fDatabase.reference.child("Images").child(currTime).setValue(downloadUrl.toString()).addOnCompleteListener { task->
                               if(task.isSuccessful){
                                   Toast.makeText(this, "Image uploaded to Database", Toast.LENGTH_SHORT).show()
                               }
                               else{
                                   Toast.makeText(this, "Error in uploading url to Database", Toast.LENGTH_SHORT).show()
                               }
                           }
                       }
                       else{
                           Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                       }
                   }
               }
                else{
                   Toast.makeText(this, Task.exception!!.message, Toast.LENGTH_SHORT).show()
               }


            }
        }
    }
}