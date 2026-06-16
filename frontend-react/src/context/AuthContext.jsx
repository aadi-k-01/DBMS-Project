import React, { createContext, useState, useEffect, useMemo } from 'react';
import { toast } from 'react-toastify';
import axios from 'axios';

export const AuthContext = createContext();

const API_BASE = "http://localhost:8000";

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
      setLoading(true);
      fetchProfile();
    } else {
      localStorage.removeItem('token');
      setCurrentUser(null);
      setLoading(false);
    }
  }, [token]);

  const fetchProfile = async () => {
    try {
      const res = await axios.get(`${API_BASE}/authservice/profile`, {
        headers: { Token: token }
      });
      if (res.data.code === 200) {
        const u = res.data.user.user;
        let role = 'patient';
        if (u.role === 2) role = 'doctor';
        if (u.role === 3) role = 'admin';
        setCurrentUser({ ...u, roleName: role });
      } else {
        logout();
      }
    } catch (err) {
      logout();
    } finally {
      setLoading(false);
    }
  };

  const login = async (username, password) => {
    try {
      const res = await axios.post(`${API_BASE}/authservice/signin`, {
        username, password
      });
      if (res.data.code === 200) {
        setLoading(true);
        setToken(res.data.jwt);
        toast.success("Welcome back!");
        return true;
      }
      toast.error(res.data.message || "Login failed");
      return false;
    } catch (err) {
      toast.error("Network error");
      return false;
    }
  };

  const register = async (userData) => {
    try {
      const res = await axios.post(`${API_BASE}/authservice/signup`, userData);
      if (res.data.code === 200) {
        toast.success("Registration successful!");
        return true;
      }
      toast.error(res.data.message);
      return false;
    } catch (err) {
      toast.error("Registration error");
      return false;
    }
  };

  const logout = () => {
    setToken(null);
    setCurrentUser(null);
  };

  const val = useMemo(() => ({
    currentUser, token, login, logout, register, loading
  }), [currentUser, token, loading]);

  return (
    <AuthContext.Provider value={val}>
      {children}
    </AuthContext.Provider>
  );
};
