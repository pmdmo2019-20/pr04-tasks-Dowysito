package es.iessaladillo.pedrojoya.pr04.data

import android.os.Build
import androidx.annotation.RequiresApi
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

// TODO: Crea una clase llamada LocalRepository que implemente la interfaz Repository
//  usando una lista mutable para almacenar las tareas.
//  Los id de las tareas se irán generando secuencialmente a partir del valor 1 conforme
//  se van agregando tareas (add).
class LocalRepository: Repository{

    private var list:MutableList<Task> = mutableListOf()

    override fun queryAllTasks(): List<Task> {
        return list
    }

    override fun queryCompletedTasks(): List<Task> {
        return list.filter { x -> x.completed }
    }

    override fun queryPendingTasks(): List<Task> {
        return list.filter { x -> !x.completed }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addTask(concept: String) {
        list.add(Task(list.size.toLong(),concept, "Created at: " + Date().toString(),false,""))
    }

    override fun insertTask(task: Task) {
        list.add(task)
    }

    override fun deleteTask(taskId: Long) {
        list.remove(list[taskId.toInt()])
    }

    override fun deleteTasks(taskIdList: List<Long>) {
        taskIdList.forEach { list.remove(list[it.toInt()]) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTaskAsCompleted(taskId: Long) {
        list[taskId.toInt()].completed=true
        list[taskId.toInt()].completedAt="Completed at: " + Date().toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTasksAsCompleted(taskIdList: List<Long>) {
        taskIdList.forEach {
            list[it.toInt()].completed=true
            list[it.toInt()].completedAt="Completed at: " + Date().toString()
        }
    }

    override fun markTaskAsPending(taskId: Long) {
        list[taskId.toInt()].completed=false
    }

    override fun markTasksAsPending(taskIdList: List<Long>) {
        taskIdList.forEach { list[it.toInt()].completed=false }
    }

}
