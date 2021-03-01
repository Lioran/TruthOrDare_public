package com.example.truthordare

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.nio.file.Paths

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val play = findViewById<Button>(R.id.play)
        val load = findViewById<Button>(R.id.load)
        val rules = findViewById<Button>(R.id.rules)
        val exit = findViewById<Button>(R.id.exit)

        play.setOnClickListener{
            val intent = Intent(this, AddPlayerActivity::class.java)
            startActivity(intent)
        }

        load.setOnClickListener{
            val intent = Intent(this, TasksActivity::class.java)
            startActivity(intent)
        }

        rules.setOnClickListener{
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
        }

        exit.setOnClickListener {
            finishAffinity()
        }
    }
}
