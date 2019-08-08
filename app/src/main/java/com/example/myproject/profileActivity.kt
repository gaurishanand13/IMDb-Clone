package com.example.myproject

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class profileActivity : AppCompatActivity() {
    var googleSignInClient:GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(searchtoolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("PROFILE")

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()

        this.googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null)
        {
            val personName = account.displayName
            val personGivenName = account.givenName
            val personEmail = account.email
            val personID = account.id
            val personPhotoUrl = account.photoUrl.toString()

            nameTextView.text = personName
            emailIdTextView.text = personEmail
            idTextView.text = personID

            Picasso.get().load(personPhotoUrl)
                .placeholder(R.drawable.loadingimage) //this image will be displayed till when the image is being downloaded from the url
                .error(R.drawable.loadingimage) //this image will be displayed when there is error while downloading the image.
                .into(profile_image);

        }

        logoutButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("ARE YOU SURE YOU WANT TO SIGN OUT")
                .setPositiveButton("YES"){DialogInterface,i ->
                    this.googleSignInClient?.signOut()?.addOnCompleteListener {
                        Toast.makeText(this,"SUCCESSFULLY SIGNED OUT",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }

                }
                .setNegativeButton("CANCEL"){DialogInterface,i->

                }
                .show()
        }
    }
}
