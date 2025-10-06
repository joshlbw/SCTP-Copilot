import React from 'react'

export function TaskItem({ task, onToggle, onRemove }) {
  return (
    <li className={`task-item ${task.done ? 'done' : ''}`}>
      <label>
        <input type="checkbox" checked={task.done} onChange={() => onToggle(task.id)} />
        <span className="task-text">{task.text}</span>
      </label>
      <button className="btn small" onClick={() => onRemove(task.id)}>Delete</button>
    </li>
  )
}

export default function TaskList({ tasks, onToggle, onRemove }) {
  if (!tasks || tasks.length === 0) {
    return <p className="empty">No tasks yet. Add one!</p>
  }

  return (
    <ul>
      {tasks.map((t) => (
        <TaskItem key={t.id} task={t} onToggle={onToggle} onRemove={onRemove} />
      ))}
    </ul>
  )
}
