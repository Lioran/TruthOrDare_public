package com.example.truthordare

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.nio.file.Paths

class TasksActivity : Activity() {

    val tasks = arrayListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        val databaseFile = getFile()
        val taskAdapter = TaskAdapter(this, tasks, databaseFile)

        val add = findViewById<FloatingActionButton>(R.id.add)
        val back = findViewById<FloatingActionButton>(R.id.backFromTasks)

        setupAddButton(add,  taskAdapter)
        setupBackButton(back)


        for (row in csvReader().readAll(databaseFile)) {
            tasks.add(Task(row[0].toLong(), row[1], row[2]))
        }
        tasks.sortBy{task -> task.id}

        val tasksList = findViewById<ListView>(R.id.listTasks)
        tasksList.adapter = taskAdapter
    }

    private fun setupBackButton(back: FloatingActionButton) {
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAddButton(add: FloatingActionButton, taskAdapter: TaskAdapter) {
        add.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_layout)

            val accept = dialog.findViewById<Button>(R.id.accept)
            val truthCheck = dialog.findViewById<CheckBox>(R.id.question_check)
            val actionCheck = dialog.findViewById<CheckBox>(R.id.action_check)
            val cancel = dialog.findViewById<Button>(R.id.cancel)
            val editText = dialog.findViewById<EditText>(R.id.editText)


            setupAcceptButton(accept, truthCheck, actionCheck, editText,  dialog, taskAdapter)
            setupTruthCheckBox(truthCheck, actionCheck)
            setupActionCheckBox(actionCheck, truthCheck)
            setupCancelButton(cancel, dialog)

            dialog.show()
        }
    }

    private fun setupCancelButton(cancel: Button, dialog: Dialog) {
        cancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun setupActionCheckBox(
        actionCheck: CheckBox,
        truthCheck: CheckBox
    ) {
        actionCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                truthCheck.isChecked = false
            }
        }
    }

    private fun setupTruthCheckBox(
        truthCheck: CheckBox,
        actionCheck: CheckBox
    ) {
        truthCheck.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                actionCheck.isChecked = false
            }
        }
    }

    private fun setupAcceptButton(
        accept: Button,
        truthCheck: CheckBox,
        actionCheck: CheckBox,
        editText: EditText,
        dialog: Dialog,
        taskAdapter: TaskAdapter
    ) {
        accept.setOnClickListener addTask@{
            if ((truthCheck.isChecked && actionCheck.isChecked) || editText.text.isBlank()) {
                Toast.makeText(this, "Ты слепой? Или притворяешься?", Toast.LENGTH_LONG).show()
                return@addTask
            }
            val typeTask = if (truthCheck.isChecked) "Правда" else "Действие"

            val databaseFile = getFile()

            val count = csvReader().readAll(databaseFile).size + 1
            csvWriter().writeAll(listOf(listOf(count.toString(), typeTask, editText.text.toString())), databaseFile, true)
            tasks.add(Task(count.toLong(), typeTask, editText.text.toString()))
            taskAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Новое задание добавлено", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
    }

    private fun getFile(): File {
        val extDir = getExternalFilesDir(null)
        val databaseFile = File(Paths.get("$extDir/database.csv").toUri())
        if (!databaseFile.exists()) databaseFile.createNewFile()
        return databaseFile
    }
}
