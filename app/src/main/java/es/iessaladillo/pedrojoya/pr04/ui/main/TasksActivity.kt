package es.iessaladillo.pedrojoya.pr04.ui.main

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.observeEvent
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.Repository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.hideKeyboard
import es.iessaladillo.pedrojoya.pr04.utils.invisibleUnless
import es.iessaladillo.pedrojoya.pr04.utils.setOnSwipeListener
import kotlinx.android.synthetic.main.tasks_activity.*


class TasksActivity : AppCompatActivity() {

    private var mnuFilter: MenuItem? = null
    private val localRepository:Repository = LocalRepository()
    private val viewModel: TasksActivityViewModel by viewModels{TasksActivityViewModelFactory(localRepository,application)}
    private val listAdapter: TasksActivityAdapter = TasksActivityAdapter().also {
        it.setOnItemClickListener {position ->
            val task:Task=it.getItem(position)
            viewModel.updateTaskCompletedState(task,task.completed)
        }
        it.setOnCheckboxClickListener {position ->
            val task:Task=it.getItem(position)
            viewModel.updateTaskCompletedState(task,task.completed)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setupRecyclerView()
        setUpViews()
        observers()
    }

    private fun observers() {
        viewModel.tasks.observe(this) {showTasks(it)}
        viewModel.activityTitle.observe(this) { this.title = it }
        viewModel.currentFilterMenuItemId.observe(this) { checkMenuItem(it) }
        viewModel.lblEmptyViewText.observe(this) { lblEmptyView.text = it }
    }

    private fun setupRecyclerView(){
        lstTasks.run{
            setHasFixedSize(true)
            layoutManager=LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context,
                RecyclerView.VERTICAL))
            adapter = listAdapter
        }

    }

    private fun removeTask(position: Int) {
        val task: Task = listAdapter.getItem(position)
        viewModel.deleteTask(task)
        Snackbar.make(lstTasks,getString(R.string.tasks_task_deleted, task.concept),Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.undo)){ viewModel.addTask(task.concept)}
            .show()
    }

    private fun setUpViews() {
        imgAddTask.setOnClickListener {
            viewModel.addTask(txtConcept.text.toString())
            it.hideKeyboard()
        }
        lstTasks.setOnSwipeListener { viewHolder, _  ->
            removeTask(viewHolder.adapterPosition)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        mnuFilter = menu.findItem(R.id.mnuFilter)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuShare -> viewModel.shareTasks()
            R.id.mnuDelete -> viewModel.deleteTasks()
            R.id.mnuComplete -> viewModel.markTasksAsCompleted()
            R.id.mnuPending -> viewModel.markTasksAsPending()
            R.id.mnuFilterAll -> viewModel.filterAll()
            R.id.mnuFilterPending -> viewModel.filterPending()
            R.id.mnuFilterCompleted -> viewModel.filterCompleted()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun checkMenuItem(@MenuRes menuItemId: Int) {
        lstTasks.post {
            val item = mnuFilter?.subMenu?.findItem(menuItemId)
            item?.let { menuItem ->
                menuItem.isChecked = true
            }
        }
    }

    private fun showTasks(tasks: List<Task>) {
        lstTasks.post {
            listAdapter.submitList(tasks)
            lblEmptyView.invisibleUnless(tasks.isEmpty())
        }
    }

}

