package com.example.truthordare

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView

class AddPlayerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_player)

        val back = findViewById<Button>(R.id.cancel_to_menu)
        val add = findViewById<Button>(R.id.add_player)
        val play = findViewById<Button>(R.id.to_play)
        val players = arrayListOf(Player(1, "Введите имя", 0), Player(2, "Введите имя", 0))
        val playerAdapter = AddPlayerAdapter(this, players)

        play.setOnClickListener{
            val intent = Intent(this, PlayActivity::class.java)
            intent.putExtra("players", players)
            startActivity(intent)
        }

        back.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        add.setOnClickListener{
            players.add(Player((players.size + 1).toLong(), "Введите имя", 0))
            playerAdapter.notifyDataSetChanged()
        }

        val playerList = findViewById<ListView>(R.id.listPlayers)
        playerList.adapter = playerAdapter
    }
}
