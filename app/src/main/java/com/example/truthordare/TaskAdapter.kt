package com.example.truthordare

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import java.nio.file.Paths

class TaskAdapter(
    context: Context,
    private val tasks: java.util.ArrayList<Task> = ArrayList(),
    private val databaseFile: File
): BaseAdapter() {

    private val lInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View? = convertView
        if (convertView == null) {
            view = lInflater.inflate(R.layout.activity_task_item, parent, false)
        }

        val task: Task= getItem(position) as Task

        val taskText = view?.findViewById<TextView>(R.id.task_item_text)
        taskText?.setText(task.type + ": " + task.content)

        val taskButton = view?.findViewById<Button>(R.id.task_item_button)

        taskButton?.setOnClickListener{
            tasks.remove(task)
            tasks.sortBy{task -> task.id}
            csvWriter().writeAll(listOf(emptyList()), databaseFile, false)
            for (row in tasks) {
                if (row == tasks[0]) csvWriter().writeAll(listOf(listOf(row.id.toString(), row.type, row.content)), databaseFile, false)
                else csvWriter().writeAll(listOf(listOf(row.id.toString(), row.type, row.content)), databaseFile, true)
            }
            notifyDataSetChanged()
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return tasks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return tasks.size
    }

}