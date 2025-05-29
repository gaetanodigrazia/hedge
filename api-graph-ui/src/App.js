import React, { useEffect, useState } from 'react';
import './App.css';

function App() {
    const [events, setEvents] = useState([]);
    const [archived, setArchived] = useState([]);
    const [deletedGroups, setDeletedGroups] = useState({});
    const [viewArchive, setViewArchive] = useState(false);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    useEffect(() => {
        fetch('/api/traces')
            .then(res => res.json())
            .then(setEvents)
            .catch(console.error);

        fetch('/api/traces/archived')
            .then(res => res.json())
            .then(setArchived)
            .catch(console.error);
    }, []);

    const getSeverityColor = (severity) => {
        switch (severity) {
            case 'INFO': return '#d1e7dd';
            case 'WARN': return '#fff3cd';
            case 'ERROR': return '#f8d7da';
            case 'CRITICAL': return '#f5c2c7';
            default: return '#ffffff';
        }
    };

    const groupBy = (items) => items.reduce((acc, event) => {
        const key = `${event.severity} | ${event.path}`;
        if (!acc[key]) acc[key] = [];
        acc[key].push(event);
        return acc;
    }, {});

    const handleDeleteGroup = (groupKey) => {
        setDeletedGroups(prev => ({ ...prev, [groupKey]: true }));
    };

    const handleArchiveGroup = (groupEvents) => {
        fetch('/api/traces/archive', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(groupEvents)
        }).then(() => window.location.reload());
    };

    const handleQuickSelect = (range) => {
        const today = new Date();
        let start, end;

        switch (range) {
            case 'today':
                start = end = today;
                break;
            case 'yesterday':
                start = end = new Date(today.setDate(today.getDate() - 1));
                break;
            case 'last7':
                start = new Date(today.setDate(today.getDate() - 6));
                end = new Date();
                break;
            default:
                return;
        }

        const toISO = d => d.toISOString().split('T')[0];
        setStartDate(toISO(start));
        setEndDate(toISO(end));
    };

    const isInvalidRange = startDate && endDate && new Date(endDate) < new Date(startDate);

    const filterByDate = (items) => {
        if (!startDate && !endDate) return items;

        return items.filter(event => {
            const eventDateLocal = new Date(event.timestamp);
            const eventDateOnly = eventDateLocal.toISOString().split('T')[0]; // Always in UTC

            // Adjust to local date using toLocaleDateString and reformat as YYYY-MM-DD
            const localDate = new Date(eventDateLocal.getTime() - eventDateLocal.getTimezoneOffset() * 60000);
            const localDateOnly = localDate.toISOString().split('T')[0];

            return (!startDate || localDateOnly >= startDate) && (!endDate || localDateOnly <= endDate);
        });
    };


    const renderGroups = (groupedData, archived = false) => (
        Object.entries(groupedData).map(([groupKey, groupEvents], i) => (
            deletedGroups[groupKey] ? null : (
                <div key={i} className="group-block">
                    <div className="group-header">
                        <h4><span className="badge">{groupKey.split('|')[0]}</span> <code>{groupKey.split('|')[1]}</code> ({groupEvents.length})</h4>
                        {!archived && (
                            <div>
                                <button className="btn-clear" onClick={() => handleDeleteGroup(groupKey)}>🗑️ Rimuovi</button>
                                <button className="btn-archive" onClick={() => handleArchiveGroup(groupEvents)}>📦 Archivia</button>
                            </div>
                        )}
                    </div>
                    <table className="trace-table">
                        <thead>
                        <tr>
                            <th>Timestamp</th>
                            <th>Method</th>
                            <th>User</th>
                            <th>Remote IP</th>
                            <th>Area</th>
                            <th>Duration (ms)</th>
                            <th>Near Limit</th>
                            <th>Error</th>
                            <th>Exception</th>
                            <th>Rate Limiter</th>
                        </tr>
                        </thead>
                        <tbody>
                        {groupEvents.map((e, j) => (
                            <tr key={j} style={{ backgroundColor: getSeverityColor(e.severity) }}>
                                <td>{e.timestamp}</td>
                                <td>{e.method}</td>
                                <td>{e.userId}</td>
                                <td>{e.remoteIp}</td>
                                <td>{e.area}</td>
                                <td>{e.durationMillis}</td>
                                <td>{e.rateLimitNearThreshold ? '⚠️' : '✔️'}</td>
                                <td>{e.exceptionOccurred ? '❌' : '✔️'}</td>
                                <td className={e.exceptionOccurred || e.exceptionName ? 'error-bold' : ''}>
                                    {e.exceptionName ?? '-'}
                                </td>
                                <td>{e.rateLimiterType ?? '-'}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )
        ))
    );

    const dataToRender = viewArchive ? archived : events;
    const filtered = isInvalidRange ? [] : filterByDate(dataToRender);
    const grouped = groupBy(filtered);

    return (
        <div className="app-layout">
            <aside className="sidebar">
                <h3>HEDGE</h3>
                <div className="date-filter">
                    <label>Dal: <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} /></label>
                    <label>Al: <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} /></label>
                    {isInvalidRange && <p className="error-msg">❌ La data di fine non può essere prima della data di inizio.</p>}
                    <div className="quick-select">
                        <button onClick={() => handleQuickSelect('today')}>Oggi</button>
                        <button onClick={() => handleQuickSelect('yesterday')}>Ieri</button>
                        <button onClick={() => handleQuickSelect('last7')}>Ultimi 7 giorni</button>
                        <button onClick={() => { setStartDate(''); setEndDate(''); }}>Reset</button>
                    </div>
                </div>
                <button className="btn-archive" onClick={() => setViewArchive(!viewArchive)}>
                    {viewArchive ? '↩️ Torna a live' : '🗂️ Vedi archiviati'}
                </button>
            </aside>
            <main className="main-content">
                {isInvalidRange
                    ? <p className="error-msg">⚠️ Intervallo date non valido.</p>
                    : renderGroups(grouped, viewArchive)
                }
            </main>
        </div>
    );
}

export default App;
