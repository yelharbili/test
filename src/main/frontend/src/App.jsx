import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import DataPage from './pages/DataPage'

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false)

    useEffect(() => {
        const token = localStorage.getItem('token')
        if (token) {
            setIsAuthenticated(true)
        }
    }, [])

    const handleLogin = (token) => {
        localStorage.setItem('token', token)
        setIsAuthenticated(true)
    }

    const handleLogout = () => {
        localStorage.removeItem('token')
        setIsAuthenticated(false)
    }

    return (
        <BrowserRouter>
            <div className="container">
                <Routes>
                    <Route
                        path="/login"
                        element={
                            isAuthenticated ? (
                                <Navigate to="/data" replace />
                            ) : (
                                <LoginPage onLogin={handleLogin} />
                            )
                        }
                    />
                    <Route
                        path="/data"
                        element={
                            isAuthenticated ? (
                                <DataPage onLogout={handleLogout} />
                            ) : (
                                <Navigate to="/login" replace />
                            )
                        }
                    />
                    <Route path="*" element={<Navigate to={isAuthenticated ? "/data" : "/login"} replace />} />
                </Routes>
            </div>
        </BrowserRouter>
    )
}

export default App
