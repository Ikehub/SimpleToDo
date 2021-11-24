package com.example.simpletodo

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    var listOfTasks = mutableListOf<String>()
    lateinit var adapter: TaskItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val onClickListener = object : TaskItemAdapter.OnClickListener {
            override fun onItemLongClicked(position: Int) {
                listOfTasks.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this@MainActivity, "Task was removed", Toast.LENGTH_SHORT).show()
                saveItems()
            }

            override fun onItemClicked(position: Int) {
                val inputTaskText = EditText(this@MainActivity)
                val oldTask = listOfTasks[position]
                inputTaskText.setText(oldTask)

                val layout = LinearLayout(this@MainActivity)
                layout.orientation = LinearLayout.VERTICAL
                layout.setPaddingRelative(40, 0, 40, 0)
                layout.addView(inputTaskText)

                val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    .setTitle("Edit Task")
                    .setMessage("Enter new task message")
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
                    .setPositiveButton("Apply", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                        val newTask = inputTaskText.text.toString()

                        if (newTask.isNotBlank() && newTask.trim() != oldTask) {
                            listOfTasks[position] = newTask.trim()
                            adapter.notifyItemChanged(position)
                            Toast.makeText(this@MainActivity, "Update successful", Toast.LENGTH_SHORT).show()
                            saveItems()
                        }

                    })
                    .setView(layout)

                dialogBuilder.create().show()
            }
        }

        loadItems()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = TaskItemAdapter(listOfTasks, onClickListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputTextField = findViewById<EditText>(R.id.addTaskField)

        findViewById<Button>(R.id.button).setOnClickListener {
            val userInputtedTask = inputTextField.text.toString()

            if (userInputtedTask.isNotBlank()) {
                listOfTasks.add(userInputtedTask.trim())

                adapter.notifyItemInserted(listOfTasks.size - 1)
                Toast.makeText(this, "Task was added", Toast.LENGTH_SHORT).show()

                saveItems()
            }
            inputTextField.setText("")
        }
    }

    // Get the file we need
    private fun getDataFile() : File {

        // Every line is going to represent a specific task in our list of tasks
        return File(filesDir, "data.txt")
    }

    // Load the items by reading every line in the data file
    private fun loadItems() {
        try {
            listOfTasks = FileUtils.readLines(getDataFile(), Charset.defaultCharset())
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    // Save items by writing them into our data file
    fun saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), listOfTasks)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

    }
}