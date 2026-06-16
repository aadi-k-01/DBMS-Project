import React, { useState, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { User, Lock, Mail, Phone, ArrowRight } from 'lucide-react';

export default function Register() {
  const { register } = useContext(AuthContext);
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ 
    fullname: '', 
    email: '', 
    password: '', 
    phone: '', 
    role: 1 
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const success = await register(formData);
    if (success) {
      navigate('/login');
    }
  };

  return (
    <div className="dashboard-container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
      <div className="glass-card animate-fade-in-up" style={{ width: '100%', maxWidth: '450px' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h2 className="animate-fade-in-up delay-1" style={{ fontSize: '1.8rem', fontWeight: 600 }}>Create Account</h2>
          <p className="animate-fade-in-up delay-2" style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>Join the premium booking experience</p>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem' }} className="animate-fade-in-up delay-2">
          
          <div style={{ position: 'relative' }}>
            <User size={20} color="var(--text-secondary)" style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)' }} />
            <input 
              type="text" 
              className="input-field" 
              placeholder="Full Name" 
              style={{ paddingLeft: '3rem' }}
              value={formData.fullname}
              onChange={(e) => setFormData({...formData, fullname: e.target.value})}
              required
            />
          </div>

          <div style={{ position: 'relative' }}>
            <Mail size={20} color="var(--text-secondary)" style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)' }} />
            <input 
              type="email" 
              className="input-field" 
              placeholder="Email Address" 
              style={{ paddingLeft: '3rem' }}
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              required
            />
          </div>

          <div style={{ position: 'relative' }}>
            <Phone size={20} color="var(--text-secondary)" style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)' }} />
            <input 
              type="text" 
              className="input-field" 
              placeholder="Phone Number" 
              style={{ paddingLeft: '3rem' }}
              value={formData.phone}
              onChange={(e) => setFormData({...formData, phone: e.target.value})}
              required
            />
          </div>

          <div style={{ position: 'relative' }}>
            <Lock size={20} color="var(--text-secondary)" style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)' }} />
            <input 
              type="password" 
              className="input-field" 
              placeholder="Password" 
              style={{ paddingLeft: '3rem' }}
              value={formData.password}
              onChange={(e) => setFormData({...formData, password: e.target.value})}
              required
            />
          </div>

          <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem' }}>
            <label style={{ color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <input type="radio" name="role" value={1} checked={formData.role === 1} onChange={() => setFormData({...formData, role: 1})} />
              Patient
            </label>
            <label style={{ color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <input type="radio" name="role" value={2} checked={formData.role === 2} onChange={() => setFormData({...formData, role: 2})} />
              Provider
            </label>
          </div>

          <button type="submit" className="btn animate-fade-in-up delay-3" style={{ width: '100%', marginTop: '1rem' }}>
            Sign Up <ArrowRight size={18} />
          </button>
        </form>

        <p className="animate-fade-in-up delay-3" style={{ textAlign: 'center', marginTop: '2rem', color: 'var(--text-secondary)' }}>
          Already have an account? <Link to="/login" style={{ color: 'var(--accent)', textDecoration: 'none', fontWeight: 500 }}>Sign In</Link>
        </p>
      </div>
    </div>
  );
}
