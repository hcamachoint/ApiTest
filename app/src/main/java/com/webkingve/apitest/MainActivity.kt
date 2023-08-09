package com.webkingve.apitest

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallAPILoginAsyncTask("alej", "1234").execute()
    }

    private inner class CallAPILoginAsyncTask(val username: String, val password: String): AsyncTask<Any, Void, String>(){
        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null

            try {
                val url = URL("https://run.mocky.io/v3/028ff4e7-45d9-4697-92b5-c8abf8f57d2b")   //https://designer.mocky.io/manage/delete/028ff4e7-45d9-4697-92b5-c8abf8f57d2b/VQ5w6HUzURZehjT1M76cOvwn0H1okHgZe1zu
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput =true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.useCaches = false
                connection.setRequestProperty("content-type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                val writeDataOutputStream = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                jsonRequest.put("username", username)
                jsonRequest.put("password", password)

                writeDataOutputStream.writeBytes(jsonRequest.toString())
                writeDataOutputStream.flush()
                writeDataOutputStream.close()

                val httpResult : Int = connection.responseCode

                if (httpResult == HttpURLConnection.HTTP_OK){   //OR 200
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?

                    try {
                        while(reader.readLine().also{line = it} != null){
                            stringBuilder.append(line + "\n")
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }

                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException){
                result = "Connection timeout"
            }catch (e: Exception){
                result = "Error: " + e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String) {    //String?
            super.onPostExecute(result)
            cancelProgressDialog()
            Log.i("JSON RESPONSE RESULT", result)

            val responseData = Gson().fromJson(result, ResponseData::class.java)
            Log.i("Id", "$responseData.id")
            Log.i("Message", responseData.message)
            Log.i("Detail First Name", "${responseData.details.first_name}")
            Log.i("Detail Last Name", "${responseData.details.last_name}")
            for (item in responseData.fruits.indices){
                Log.i("Item", "${responseData.fruits[item]}")
            }

            /*//GET OBJECT
            val jsonObject = JSONObject(result) //Convert response in JSON OBJECT

            //SINGLE INT
            val id = jsonObject.optInt("id")   //Accessing to 'id', this is contains in JSON object
            Log.i("id", "$id")

            //SINGLE STRING
            val message = jsonObject.optString("message")   //Accessing to 'message', this is contains in JSON object
            Log.i("Message", message)

            //DETAIL OBJECT
            val details = jsonObject.optJSONObject("details")
            val detailFirstName = details.optString("first_name")
            val detailLastName = details.optString("last_name")
            Log.i("First name", detailFirstName)
            Log.i("Last name", detailLastName)

            //LIST ARRAY
            val fruits = jsonObject.optJSONArray("fruits")   //Accessing to 'fruits', this is contains in JSON object
            Log.i("Fruits size", "${fruits.length()}")
            for(item in 0 until fruits.length()){
                Log.i("Value $item", "${fruits[item]}")

                //Prepare to acces to data
                val dataItemObject: JSONObject = fruits[item] as JSONObject

                //Get and Print fruit name
                val name = dataItemObject.optString("name")
                Log.i("Fruit name", "$name")

                //Get and Print Fruit color
                val color = dataItemObject.optString("color")
                Log.i("Fruit color", "$color")
            }*/
        }

        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }

    }
}