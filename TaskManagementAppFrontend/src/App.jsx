import React, { useEffect, useState, useRef } from 'react'

function useLocalStorage(key, initial) {
  const [state, setState] = useState(() => {
    try {
      const raw = localStorage.getItem(key)
      return raw ? JSON.parse(raw) : initial
    } catch (e) {
      return initial
    }
  })

  useEffect(() => {
    try {
      localStorage.setItem(key, JSON.stringify(state))
    } catch (e) {}
  }, [key, state])

  return [state, setState]
}

import TaskList from './components/TaskList'

export default function App() {
  const [tasks, setTasks] = useLocalStorage('tasks.v1', [])
  const [text, setText] = useState('')
  const inputRef = useRef(null)

  function addTask(e) {
    e.preventDefault()
    const t = text.trim()
    if (!t) return
    setTasks([{ id: Date.now(), text: t, done: false }, ...tasks])
    setText('')
    // keep focus on input for quick entry
    inputRef.current?.focus()
  }

  function toggleTask(id) {
    setTasks(tasks.map(t => (t.id === id ? { ...t, done: !t.done } : t)))
  }

  function removeTask(id) {
    setTasks(tasks.filter(t => t.id !== id))
  }

  return (
    <div className="app">
      <header>
        <h1>Task Management App</h1>
      </header>

      <main>
        <form onSubmit={addTask} className="add-form">
          <input
            ref={inputRef}
            autoFocus
            value={text}
            onChange={e => setText(e.target.value)}
            placeholder="Add a new task"
            aria-label="New task"
          />
          <button className="btn" type="submit" disabled={!text.trim()}>Add</button>
        </form>

        <section className="tasks">
          <TaskList tasks={tasks} onToggle={toggleTask} onRemove={removeTask} />
        </section>
      </main>

      <footer>
        <small>Tasks persisted to localStorage</small>
      </footer>
    </div>
  )
}
