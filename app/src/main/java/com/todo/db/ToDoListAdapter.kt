/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.todo.db

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.todo.R


class ToDoListAdapter internal constructor(
        context: Context
) : RecyclerView.Adapter<ToDoListAdapter.WordViewHolder>(), Filterable {

    private val CHECKBOX = "$%$##"
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var words = emptyList<ToDo>() // Cached copy of words

    private var filterlist = mutableListOf<ToDo>()

    init {
        filterlist = words.toMutableList()
    }
    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)
        var desc: TextView = itemView.findViewById(R.id.desc)
        var checkBox: CheckBox = itemView.findViewById(R.id.checkbox)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.todo_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = filterlist[position]
        holder.title.text = current.word.substringBefore("|")
        holder.desc.text = current.word.substringAfter("|")
//        holder.checkBox.isChecked = false

        if(current.word.contains(CHECKBOX)){
//            holder.checkBox.isChecked = true
            holder.title.setTextColor(Color.GRAY)
            holder.title.paintFlags = holder.title.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            holder.checkBox.isChecked = false
        }
        holder.title.text = current.word.substringBefore("|").replace(CHECKBOX,"")
        holder.desc.text = current.word.substringAfter("|").replace(CHECKBOX,"")


        holder.checkBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(holder.checkBox.isChecked && !current.word.contains(CHECKBOX)){
                val updatedtoDo = ToDo(current.word + CHECKBOX)
                filterlist.removeAt(position)
                filterlist.add(updatedtoDo)

                notifyDataSetChanged()
            }

        }

    }

    internal fun setWords(toDos: List<ToDo>) {
        this.words = toDos
        this.filterlist = toDos.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = filterlist.size

    override fun getFilter(): Filter {
        return todoFilter
    }

    private val todoFilter: Filter = object : Filter(){


        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val resultList: MutableList<ToDo> = ArrayList()

            val charSearch = constraint.toString()
            if (charSearch.isNullOrEmpty()) {
                filterlist = words.toMutableList()
            }else{
                val filterPattern =
                    constraint.toString().toLowerCase().trim { it <= ' ' }
                for (item in words) {
                    if (item.word.toLowerCase().contains(filterPattern)) {
                        resultList.add(item)
                    }
                }
                filterlist = resultList
            }

            val results = FilterResults()
            results.values = filterlist
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {

            filterlist = results.values as MutableList<ToDo>
            notifyDataSetChanged()
        }

    }
}


