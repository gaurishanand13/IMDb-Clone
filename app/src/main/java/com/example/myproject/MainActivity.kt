package com.example.myproject

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.example.myproject.RetrofitDataClasses.RetrofitClient
import com.example.myproject.RetrofitDataClasses.movieVideoDetails
import com.example.myproject.loginDetails.answerOfLoginDetailsValidity
import com.example.myproject.loginDetails.loginValidityCheck
import com.example.myproject.loginDetails.sessionIdDetails
import com.example.myproject.loginDetails.tokenDataClass
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.mainfile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {


    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    /*
    val RC_SIGN_IN_REQUESTCODE = 0
    var GoogleSignInClient : GoogleSignInClient? = null
    var shimmer = Shimmer()*/

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Till now result has been returned from the intent where the user was given the options of gmal if though which
        // he/she wants to log in
        if(requestCode == RC_SIGN_IN_REQUESTCODE)
        {
            val google_Sign_In_Account_Task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            // Now we can eaily get the object of type googleSignInAccount if the user has successfully logged in
            try{

                val googleSignInAccount : GoogleSignInAccount? = google_Sign_In_Account_Task.getResult(ApiException::class.java)
                startActivity(Intent(this,LoggedInActivity::class.java))

            }catch (e:ApiException){
                Log.i("TAG","signInResult:failed code " + e.statusCode)
                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
            }
        }
    }
    */
    override fun onStart()
    {
        /*
        val account = GoogleSignIn.getLastSignedInAccount(this)
        //if the user has previously logged in and has not logged out from the account then this account variable won't be null
        if(account!=null)
        {
            startActivity(Intent(this,LoggedInActivity::class.java))
        }*/
        super.onStart()
    }


    fun see_whether_valid_email_and_password_or_not(requestToken : String) {

        val x = loginValidityCheck(usernameEditText.text.toString(),passwordEditText.text.toString(),requestToken)
        val call = RetrofitClient.retrofitService?.validateRequestToken(x)
        call?.enqueue(object : Callback<answerOfLoginDetailsValidity>{
            override fun onFailure(call: Call<answerOfLoginDetailsValidity>, t: Throwable) {
                Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_LONG).show()
                rotateloading.stop()
                loginButton.isEnabled = true
                usernameEditText.isEnabled = true
                passwordEditText.isEnabled = true
            }

            override fun onResponse(call: Call<answerOfLoginDetailsValidity>, response: Response<answerOfLoginDetailsValidity>) {

                if(response.isSuccessful)
                {
                    val data = response.body()
                    if(data!!.success == true)
                    {
                        //Create a session id using that email id
                        createSessionID(requestToken,usernameEditText.text.toString())
                    }
                }

                else
                {
                    Toast.makeText(this@MainActivity,"Invalid username and/or password",Toast.LENGTH_SHORT).show()
                    rotateloading.stop()
                    loginButton.isEnabled = true
                    usernameEditText.isEnabled = true
                    passwordEditText.isEnabled = true

                }

            }

        })

    }

    fun createSessionID(requestToken : String,username : String)
    {
        val call = RetrofitClient.retrofitService?.createSessionId(requestToken)
        call?.enqueue(object : Callback<sessionIdDetails>{
            override fun onFailure(call: Call<sessionIdDetails>, t: Throwable) {

            }

            override fun onResponse(call: Call<sessionIdDetails>, response: Response<sessionIdDetails>) {
                if(response.isSuccessful)
                {
                    //Now Store this sessionId in the shared preference of the app
                    val data = response.body()
                    val preferences = getSharedPreferences("myPref",Context.MODE_PRIVATE)
                    preferences.edit().putString("sessionID",data!!.session_id).apply()
                    preferences.edit().putString("userName",username).apply()
                    rotateloading.stop()
                    val intent = Intent(this@MainActivity,LoggedInActivity::class.java)
                    intent.putExtra("userName",username)
                    Toast.makeText(this@MainActivity,"LOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show()
                    startActivity(intent)

                    //Now resetting all the variables if the user comes backs to this activity after logging out all the things
                    rotateloading.stop()
                    loginButton.isEnabled = true
                    usernameEditText.isEnabled = true
                    passwordEditText.isEnabled = true
                    usernameEditText.text = Editable.Factory.getInstance().newEditable("")
                    passwordEditText.text = Editable.Factory.getInstance().newEditable("")

                }
            }

        })
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("myPref",Context.MODE_PRIVATE)
        if(preferences.getString("sessionID","").equals(""))
        {
        }
        else
        {
            val pref = getPreferences(Context.MODE_PRIVATE)
            val intent = Intent(this@MainActivity,LoggedInActivity::class.java)
            intent.putExtra("userName",pref.getString("userName",""))
            //Toast.makeText(this@MainActivity,"LOGIN SUCCESSFULL",Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        setContentView(R.layout.activity_main)



        shimmer_tv.startShimmer()

        createAnAccountButton.setOnClickListener {
            val intent = Intent(this,webViewActivity::class.java)
            intent.putExtra("url","https://www.themoviedb.org/account/signup")
            startActivity(intent)
        }

        loginButton.setOnClickListener {

            if(usernameEditText.text.toString().equals("") || passwordEditText.text.toString().equals(""))
            {
                Toast.makeText(this,"Enter both password and username",Toast.LENGTH_SHORT).show()
            }else {
                loginButton.isEnabled = false
                usernameEditText.isEnabled = false
                passwordEditText.isEnabled = false
                rotateloading.start()
                val call = RetrofitClient.retrofitService?.getRequestToken()
                call?.enqueue(object  : Callback<tokenDataClass> {
                    override fun onFailure(call: Call<tokenDataClass>, t: Throwable) {
                        Toast.makeText(this@MainActivity,t.message,Toast.LENGTH_LONG).show()
                        rotateloading.stop()
                        loginButton.isEnabled = true
                        usernameEditText.isEnabled = true
                        passwordEditText.isEnabled = true
                    }

                    override fun onResponse(call: Call<tokenDataClass>, response: Response<tokenDataClass>) {
                        if(response.isSuccessful)
                        {
                            val data = response.body()
                            var requestToken = data!!.request_token
                            see_whether_valid_email_and_password_or_not(requestToken)
                        }
                    }
                })

            }

        }
        /*
        //First we will set up all the things required when the perfon want to sign in.

        //Since we just want basic information from the user ,therefore we will get basic parameters only using DEFAULT_SIGN_IN parameter
        //We will now configure sign in to request user's id.email address and basic profile .
        //ID and other basic profile properties are included in DEFAULT_SIGN_IN.
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()
        //Doubt what does request id token does?

        //Note - Now we will set up the google signIn client
        GoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions)

        googleSignIN.setOnClickListener {
            Toast.makeText(this,"HEY BUDDY",Toast.LENGTH_LONG).show()
           //When this button is clicked. It means user wants to sign in using gmail. Therefore we will get the user to another activity using intent
            //where the user can log in to the gmail. Starting the intent prompts the user to select a Google account to sign in with. If you requested
            // scopes beyond profile, email, and openid, the user is also prompted to grant access to the requested resources.





            val intent = GoogleSignInClient?.signInIntent
            startActivityForResult(intent,RC_SIGN_IN_REQUESTCODE) //Here RC_SIGN_IN is the request code
        }

        */

    }
}
