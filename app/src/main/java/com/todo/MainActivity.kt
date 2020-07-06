package com.todo

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todo.db.ToDo
import com.todo.db.ToDoListAdapter
import com.todo.db.ToDoViewModel


//Created by -Nikhil Shrivastava 07/07/2020

class MainActivity : AppCompatActivity() {

    private val newTodoActivityRequestCode = 1
    lateinit var recyclerView: RecyclerView
    lateinit var fab: FloatingActionButton
    lateinit var searchView: SearchView
    lateinit var adapter: ToDoListAdapter

    private val toDoViewModel: ToDoViewModel by lazy {
        ViewModelProvider(this).get(ToDoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        adapter = ToDoListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        toDoViewModel.allWords.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })
    }

    fun initViews() {
        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recyclerview)

        fab.setOnClickListener { view ->
            startTodo()
        }
    }


    private fun startTodo() {
        val intent = Intent(this@MainActivity, AddToDoActivity::class.java)
        startActivityForResult(intent, newTodoActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newTodoActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val word = ToDo(data.getStringExtra(AddToDoActivity.EXTRA_REPLY) ?: "")
                toDoViewModel.insert(word)
                Unit
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem = menu?.findItem(R.id.action_search)!!
        if (searchItem != null) {
            searchView = MenuItemCompat.getActionView(searchItem) as SearchView
            searchView.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    return true
                }
            })

            val searchPlate =
                searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
            searchPlate.hint = "Search"
            val searchPlateView: View =
                searchView.findViewById(androidx.appcompat.R.id.search_plate)

            searchPlateView.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.transparent
                )
            )


            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Toast.makeText(applicationContext, query, Toast.LENGTH_SHORT).show()
                    adapter.getFilter().filter(query);
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let{
                           adapter.getFilter().filter(newText);
                    }

                    return false
                }
            })

            val searchManager =
                getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return super.onCreateOptionsMenu(menu)
    }
}