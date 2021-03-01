package com.example.truthordare

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.TextView
import android.widget.Toast
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.field_for_task.*
import java.io.File
import java.nio.file.Paths
import java.util.logging.Logger

class PlayActivity : Activity() {

    private val truths = arrayListOf<Task>()
    private val actions = arrayListOf<Task>()
    var isTruthsEnd = false
    var isActionsEnd = false
    private val punishments = arrayListOf(
        "50 отжиманий с весом на спине. Вес может быть живым)",
        "Пей то что пожелаешь, пока весь стыд не выйдет из тебя",
        "Пей то что пожелаешь, только не сам, с чужих рук",
        "50 приседаний, на бутылку или нет - решать тебе",
        "50 подходов на пресс, сгоняй жир!",
        "Пей то что пожелаешь, через воронку",
        "Пей то что пожелаешь, через воронку",
        "Спой самую топовую песню",
        "Станцуй танец маленьких утят",
        "Спой 'We are the champions' - Queen",
        "Спой песню 'Мужицкий дождь'",
        "Стой в планке 1 минуту",
        "Добавь в свой напиток соль и сахар по вкусу, и выпей",
        "Выбери свою любимую песню, отбей ее ритм на своем животе",
        "Выпей 3 шота своего напитка",
        "До конца игры ты добавляешь окончания 'кун', 'тян' к имени того, к кому обращаешься",
        "Ты получаешь титул 'Вождь племени ссыкунят', каждый раз когда будешь рассказывать что-то от своего лица говори 'Я как Вождь племени ссыкунят...'",
        "Поздравляем ты поменял/а пол, смени имя на женский/мужской аналог. Теперь к тебе должны обращаться только по этому имени.",
        "Ты боишься как страус, поэтому засунь голову куда-нибудь под подушку, оттопырь жопу, пусть тебя по ней ударят",
        "Ты наказан - постой в углу минут 10"
    )
    private var lastPunishment = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val extDir = getExternalFilesDir(null)
        val databaseFile = File(Paths.get("$extDir/database.csv").toUri())

        for (row in csvReader().readAll(databaseFile)) {
            if (row[1] == "Правда") truths.add(Task(row[0].toLong(), row[1], row[2]))
            else actions.add(Task(row[0].toLong(), row[1], row[2]))
        }

        val startAgain = findViewById<Button>(R.id.start_again)
        val truth = findViewById<Button>(R.id.truth_button)
        val action = findViewById<Button>(R.id.action_button)
        val layoutForTask = findViewById<LinearLayout>(R.id.layout_for_task)
        val next = findViewById<Button>(R.id.next)
        val punishmentButton = findViewById<Button>(R.id.punishment_button)
        val taskText = findViewById<TextView>(R.id.task_text)
        val punishmentText = findViewById<TextView>(R.id.punishment_text)

        val arguments = intent.extras
        val players: ArrayList<Player> = arguments?.get("players") as ArrayList<Player>

        val currentPlayerName = findViewById<TextView>(R.id.current_player_name)

        startAgain.setOnClickListener{
            val intent = Intent(this, AddPlayerActivity::class.java)
            startActivity(intent)
        }

        var counter = 0

        var currentPlayer: Player = players[counter]
        currentPlayerName.text = "Текущий игрок: " + currentPlayer.name

        checkPlayerTwoTruth(currentPlayer, truth, action)

        truth.setOnClickListener{
            truth.visibility = View.GONE
            action.visibility = View.GONE
            layoutForTask.visibility = View.VISIBLE

            setTruthTask()

            currentPlayer.twoTruth = currentPlayer.twoTruth.inc()
        }

        action.setOnClickListener{
            truth.visibility = View.GONE
            action.visibility = View.GONE
            layoutForTask.visibility = View.VISIBLE

            setActionTask()
        }

        next.setOnClickListener{
            punishmentButton.visibility = View.VISIBLE
            taskText.visibility = View.VISIBLE
            punishmentText.visibility = View.GONE
            counter = counter.inc()
            if (counter == players.size) counter = 0
            currentPlayer = players[counter]
            currentPlayerName.text = "Текущий игрок: " + currentPlayer.name
            if (checkTasks(truth, action, currentPlayerName)) return@setOnClickListener
            if (checkPlayerTwoTruth(currentPlayer, truth, action)) return@setOnClickListener
            truth.visibility = View.VISIBLE
            action.visibility = View.VISIBLE
            layoutForTask.visibility = View.GONE
        }

        punishmentButton.setOnClickListener{
            if (punishments.isEmpty()) {
                punishments.addAll(lastPunishment)
                lastPunishment.removeAll(lastPunishment)
            }
            taskText.visibility = View.GONE
            punishmentText.visibility = View.VISIBLE
            punishmentButton.visibility = View.GONE
            var punishment = punishments.random()
            punishments.remove(punishment)
            lastPunishment.add(punishment)
            punishmentText.text = "Наказание: " + punishment
        }
    }

    private fun setActionTask() {
        val taskText = findViewById<TextView>(R.id.task_text)

        val task = actions.random()
        actions.removeIf { currentTask -> currentTask.id == task.id }
        taskText.text = task.content
    }

    private fun setTruthTask() {
        val taskText = findViewById<TextView>(R.id.task_text)

        val task = truths.random()
        truths.removeIf { currentTask -> currentTask.id == task.id }
        taskText.text = task.content
    }

    private fun checkPlayerTwoTruth(
        currentPlayer: Player,
        truth: Button,
        action: Button
    ): Boolean {
        val layoutForTask = findViewById<LinearLayout>(R.id.layout_for_task)
        if (currentPlayer.twoTruth == 2) {
            currentPlayer.twoTruth = 0
            truth.visibility = View.GONE
            action.visibility = View.GONE
            layoutForTask.visibility = View.VISIBLE

            setActionTask()

            Toast.makeText(this, "Хорош бздеть пора выполнить хоть какое-то действие", Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }

    private fun checkTasks(
        truth: Button,
        action: Button,
        currentPlayerName: TextView
    ): Boolean {
        val layoutForTask = findViewById<LinearLayout>(R.id.layout_for_task)
        val taskText = findViewById<TextView>(R.id.task_text)

        truth.visibility = View.GONE
        action.visibility = View.GONE
        layoutForTask.visibility = View.VISIBLE
        if (truths.isEmpty() && actions.isEmpty()) {
            currentPlayerName.visibility = View.GONE
            taskText.text = "А вы не простые гуси как оказалось на первый взгляд)\n Поздравляю вы прошли все задания, надеюсь вы сможете жить после всего того что пережили"
            next.visibility = View.GONE
            val punishmentText = findViewById<TextView>(R.id.punishment_text)
            val punishmentButton = findViewById<Button>(R.id.punishment_button)
            punishmentButton.visibility = View.GONE
            punishmentText.visibility = View.GONE
            return true
        } else if (truths.isEmpty()) {
            setActionTask()
            if (!isTruthsEnd) {
                Toast.makeText(this, "Всю правду разбазарили, остались только действия", Toast.LENGTH_LONG).show()
                isTruthsEnd = true
            }
            return true
        } else if (actions.isEmpty()) {
            setTruthTask()
            if (!isActionsEnd) {
                Toast.makeText(this, "Все действия разбазарили, осталась только правда", Toast.LENGTH_LONG).show()
                isActionsEnd = true
            }
            return true
        }
        return false
    }
}
