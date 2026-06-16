import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../context/AuthContext';
import { LogOut, Calendar, Clock, Star, Settings } from 'lucide-react';
import axios from 'axios';
import { toast } from 'react-toastify';

const API_BASE = "http://localhost:8000";

export default function PatientDashboard() {
  const { currentUser, token, logout } = useContext(AuthContext);
  
  const [appointments, setAppointments] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [pref, setPref] = useState('Morning');
  const [loading, setLoading] = useState(true);

  const fetchData = async () => {
    try {
      setLoading(true);
      // Fetch History
      const histRes = await axios.get(`${API_BASE}/bookingservice/getclienthistory`, { headers: { Token: token }});
      if(histRes.data.code === 200) setAppointments(histRes.data.appointments || []);
      
      // Fetch Preferences
      const prefRes = await axios.get(`${API_BASE}/authservice/preferences`, { headers: { Token: token }});
      if(prefRes.data.code === 200 && prefRes.data.preference) {
        setPref(prefRes.data.preference.preferredTimeOfDay || 'Morning');
      }

      // Fetch Suggestions
      const sugRes = await axios.get(`${API_BASE}/bookingservice/suggestions`, { headers: { Token: token }});
      if(sugRes.data.code === 200) setSuggestions(sugRes.data.suggestions || []);
      
    } catch (err) {
      toast.error("Error loading dashboard data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const savePreference = async () => {
    try {
      const res = await axios.post(`${API_BASE}/authservice/preferences`, {
        preferredTimeOfDay: pref,
        emailNotificationsEnabled: true
      }, { headers: { Token: token }});
      
      if(res.data.code === 200) {
        toast.success("Preferences updated! Refreshing suggestions...");
        fetchData(); // reload suggestions based on new pref
      }
    } catch (err) {
      toast.error("Failed to save preference");
    }
  };

  const bookSlot = async (slot) => {
    try {
      const res = await axios.post(`${API_BASE}/bookingservice/createappointment`, {
        timeSlotId: slot.id,
        notes: "Intelligent Booking"
      }, { headers: { Token: token }});
      
      if(res.data.code === 200) {
        toast.success("Appointment Booked Successfully!");
        fetchData();
      } else {
        toast.error(res.data.message);
      }
    } catch (err) {
      toast.error("Failed to book slot");
    }
  };

  return (
    <div className="dashboard-container animate-fade-in-up">
      <div className="header delay-1">
        <div>
          <h1 style={{ fontSize: '2rem', fontWeight: 600 }}>Welcome, {currentUser?.fullname}</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Patient Dashboard</p>
        </div>
        <button className="btn btn-secondary" onClick={logout}>
          <LogOut size={18} /> Logout
        </button>
      </div>

      <div className="grid-2">
        {/* Left Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          
          {/* Preferences Card */}
          <div className="glass-card animate-fade-in-up delay-2">
            <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
              <Settings size={22} color="var(--accent)" /> Preferences
            </h2>
            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
              <select className="input-field" value={pref} onChange={e => setPref(e.target.value)}>
                <option value="Morning">Morning (06:00 - 12:00)</option>
                <option value="Afternoon">Afternoon (12:00 - 17:00)</option>
                <option value="Evening">Evening (17:00+)</option>
              </select>
              <button className="btn" onClick={savePreference}>Save</button>
            </div>
          </div>

          {/* Intelligent Suggestions Card */}
          <div className="glass-card animate-fade-in-up delay-3" style={{ borderLeft: '4px solid var(--accent)' }}>
            <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
              <Star size={22} color="var(--accent)" /> AI Suggested Slots
            </h2>
            {loading ? <p>Loading suggestions...</p> : suggestions.length === 0 ? <p>No suggestions available.</p> : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {suggestions.map((s, idx) => (
                  <div key={idx} style={{ background: 'rgba(255,255,255,0.02)', padding: '1rem', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <h4 style={{ color: 'var(--text-primary)' }}>{s.providerName}</h4>
                      <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <Calendar size={14}/> {s.slotDate} | <Clock size={14}/> {s.startTime}
                      </p>
                    </div>
                    <button className="btn" style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }} onClick={() => bookSlot(s)}>
                      Book Now
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>

        {/* Right Column */}
        <div className="glass-card animate-fade-in-up delay-2">
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
            <Calendar size={22} color="var(--accent)" /> My Appointments
          </h2>
          {loading ? <p>Loading history...</p> : appointments.length === 0 ? <p>No appointments booked yet.</p> : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {appointments.map((a, idx) => (
                <div key={idx} style={{ borderBottom: '1px solid var(--glass-border)', paddingBottom: '1rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <h4 style={{ fontWeight: 500 }}>{a.providerName || 'Provider'}</h4>
                    <span className={`tag ${a.status === 'BOOKED' ? 'tag-success' : 'tag-pending'}`}>{a.status}</span>
                  </div>
                  <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '0.5rem' }}>
                    {a.slotDate} at {a.startTime}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
