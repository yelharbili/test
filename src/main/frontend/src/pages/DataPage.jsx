import { useState, useEffect } from 'react'
import axios from 'axios'

function DataPage({ onLogout }) {
    const [data, setData] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [filter, setFilter] = useState('')

    const fetchData = async (condition = '') => {
        setLoading(true)
        setError('')
        try {
            const token = localStorage.getItem('token')
            const params = condition ? { p_condition: condition } : {}
            // Appel dynamique: route "products" définie dans routes.json
            const response = await axios.get('/api/rpc/products', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params,
            })
            setData(response.data)
        } catch (err) {
            if (err.response?.status === 401 || err.response?.status === 403) {
                onLogout()
            } else {
                setError(err.response?.data?.message || 'Erreur lors du chargement des données')
            }
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchData()
    }, [])

    const handleFilter = () => {
        fetchData(filter)
    }

    return (
        <div className="data-container">
            <div className="data-header">
                <h2>Données de la Table</h2>
                <button className="btn btn-logout" onClick={onLogout}>
                    Déconnexion
                </button>
            </div>

            <div className="filter-container">
                <input
                    type="text"
                    placeholder="Entrez une condition (ex: ACTIVE)"
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                />
                <button onClick={handleFilter}>Filtrer</button>
            </div>

            {error && <div className="error-message">{error}</div>}

            {loading ? (
                <div className="loading">Chargement...</div>
            ) : (
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nom</th>
                            <th>Description</th>
                            <th>Statut</th>
                        </tr>
                    </thead>
                    <tbody>
                        {data.length === 0 ? (
                            <tr>
                                <td colSpan="4" style={{ textAlign: 'center' }}>
                                    Aucune donnée trouvée
                                </td>
                            </tr>
                        ) : (
                            data.map((item) => (
                                <tr key={item.id}>
                                    <td>{item.id}</td>
                                    <td>{item.name}</td>
                                    <td>{item.description}</td>
                                    <td>{item.status}</td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            )}
        </div>
    )
}

export default DataPage
