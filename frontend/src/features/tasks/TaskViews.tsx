import type { Task } from './types'
import { states, priorities, currentMonth } from './types'
export type TaskView = 'list' | 'kanban' | 'calendar'
export default function TaskViews({ tasks, view, onView, month, onMonth, onOpen }: {
  tasks: Task[]; view: TaskView; onView: (view: TaskView) => void; month: string; onMonth: (month: string) => void; onOpen: (id: string) => void
}) {
  function card(task: Task) { return <button className="task-card" aria-label={task.titulo} key={task.id} onClick={() => onOpen(task.id)}>
    <strong>{task.titulo}</strong><span>{states[task.estado]} · {task.prioridade ? priorities[task.prioridade] : 'Sem prioridade'} · {task.prazo ?? 'Sem prazo'}</span>
  </button> }
  const [year, number] = month.split('-').map(Number)
  function dateAt(y: number, m: number, d = 1) { const date = new Date(0); date.setFullYear(y, m, d); date.setHours(0, 0, 0, 0); return date }
  const first = dateAt(year, number - 1)
  const offset = (first.getDay() + 6) % 7
  const days = dateAt(year, number, 0).getDate()
  function shift(amount: number) {
    const date = dateAt(year, number - 1 + amount)
    if (date.getFullYear() >= 1 && date.getFullYear() <= 9999) onMonth(`${String(date.getFullYear()).padStart(4, '0')}-${String(date.getMonth() + 1).padStart(2, '0')}`)
  }
  return <>
    <nav className="actions" aria-label="Vistas de tarefas">{([['list', 'Lista'], ['kanban', 'Kanban'], ['calendar', 'Calendário']] as const).map(([value, label]) =>
      <button key={value} aria-pressed={view === value} onClick={() => onView(value)}>{label}</button>)}</nav>
    {view === 'list' ? tasks.length === 0 ? <p>Nenhuma tarefa encontrada.</p> : <ul className="task-list">{tasks.map(task => <li key={task.id}>{card(task)}</li>)}</ul>
      : view === 'kanban' ? <div className="kanban">{Object.entries(states).map(([state, label]) => <section key={state} aria-label={`Coluna ${label}`}>
        <h4>{label} ({tasks.filter(task => task.estado === state).length})</h4>
        {tasks.filter(task => task.estado === state).map(card)}
        {!tasks.some(task => task.estado === state) && <p>Sem tarefas neste estado.</p>}
      </section>)}</div> : <section aria-label="Calendário de tarefas">
        <label htmlFor="calendar-month">Mês do calendário</label><input id="calendar-month" type="month" value={month} min="0001-01" max="9999-12" onChange={event => { if (/^\d{4}-\d{2}$/.test(event.target.value)) onMonth(event.target.value) }} />
        <div className="actions"><button onClick={() => shift(-1)}>Mês anterior</button><button onClick={() => onMonth(currentMonth())}>Mês atual</button><button onClick={() => shift(1)}>Mês seguinte</button></div>
        <p>{first.toLocaleDateString('pt-PT', { month: 'long', year: 'numeric' })}. As tarefas sem prazo permanecem na lista e no Kanban.</p>
        <div className="calendar-grid">
          {['Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb', 'Dom'].map(day => <strong key={day}>{day}</strong>)}
          {Array.from({ length: offset }, (_, i) => <div key={`blank-${i}`} />)}
          {Array.from({ length: days }, (_, i) => {
            const date = `${month}-${String(i + 1).padStart(2, '0')}`
            const dated = tasks.filter(task => task.prazo === date)
            return <section key={date} aria-label={date}><time dateTime={date}>{i + 1}</time>{dated.map(card)}{dated.length > 0 && <span className="day-count">{dated.length} {dated.length === 1 ? 'tarefa' : 'tarefas'}</span>}</section>
          })}
        </div>
        <div className="calendar-agenda"><h4>Prazos neste mês</h4><ul className="task-list">{tasks.filter(task => task.prazo?.startsWith(`${month}-`)).sort((a, b) => a.prazo!.localeCompare(b.prazo!) || a.id.localeCompare(b.id)).map(task => <li key={task.id}>
          <time dateTime={task.prazo!}>{task.prazo}</time>{card(task)}
        </li>)}</ul></div>
        {!tasks.some(task => task.prazo?.startsWith(`${month}-`)) && <p>Sem tarefas com prazo neste mês para estes filtros.</p>}
      </section>}
  </>
}
