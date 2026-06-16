import React, { useContext } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { AuthProvider, AuthContext } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import PatientDashboard from './pages/PatientDashboard';
import DoctorDashboard from './pages/DoctorDashboard';

const PrivateRoute = ({ children }) => {
  const { currentUser, loading } = useContext(AuthContext);
  if (loading) return <div style={{color:'white', padding:'2rem'}}>Loading...</div>;
  if (!currentUser) return <Navigate to="/login" />;
  return children;
};

const DashboardRouter = () => {
  const { currentUser, loading } = useContext(AuthContext);
  if (loading) return <div style={{color:'white', padding:'2rem'}}>Loading...</div>;
  if (!currentUser) return <Navigate to="/login" />;

  if (currentUser.roleName === 'doctor') return <DoctorDashboard />;
  return <PatientDashboard />;
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <ToastContainer theme="dark" position="top-right" />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={
            <PrivateRoute>
              <DashboardRouter />
            </PrivateRoute>
          } />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
