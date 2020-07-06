package com.todo


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

//Created by -Nikhil Shrivastava 07/07/2020

class AddToDoActivity : AppCompatActivity(){

    lateinit var editTitle: EditText
    lateinit var editDesc: EditText

    companion object {
        const val EXTRA_REPLY = "com.todo.addtodo"
    }

    public override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        setContentView(R.layout.addtodo_activity)

        editTitle = findViewById<EditText>(R.id.title)
        editDesc = findViewById<EditText>(R.id.desc)
        val submit = findViewById<Button>(R.id.submit)
        val cancel = findViewById<Button>(R.id.cancel)
        submit.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editTitle.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                var desc = ""
                if(!TextUtils.isEmpty(editDesc.text)) {
                    desc = editDesc.text.toString()
                }
                val word = editTitle.text.toString() + "|" + desc
                replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
        cancel.setOnClickListener{
            finish()
        }
    }
}