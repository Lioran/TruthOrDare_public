package com.example.truthordare

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener

class AddPlayerAdapter(
    context: Context,
    private val players: java.util.ArrayList<Player> = ArrayList()
) : BaseAdapter() {

    private val lInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (convertView == null) {
            view = lInflater.inflate(R.layout.activity_add_player_item, parent, false)
        }

        val player: Player = getItem(position) as Player

        val name = view?.findViewById<TextView>(R.id.player_name)
        name?.text = "Игрок " + player.id

        val editName = view?.findViewById<EditText>(R.id.edit_player_name)
        editName?.hint = player.name

        editName?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    player.name = s.toString()
                }
            }
        )

        return view!!
    }

    override fun getItem(position: Int): Any {
        return players[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return players.size
    }
}