import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../context/AuthContext';
import { LogOut, Calendar, Plus, Users } from 'lucide-react';
import axios from 'axios';
import { toast } from 'react-toastify';

const API_BASE = "http://localhost:8000";

export default function DoctorDashboard() {
  const { currentUser, token, logout } = useContext(AuthContext);
  
  const [schedule, setSchedule] = useState([]);
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);

  const [newSlot, setNewSlot] = useState({ date: '', start: '', end: '' });

  const fetchData = async () => {
    try {
      setLoading(true);
      // Fetch Schedule (Appointments booked with this doctor)
      const schedRes = await axios.get(`${API_BASE}/bookingservice/getproviderschedule`, { headers: { Token: token }});
      if(schedRes.data.code === 200) setSchedule(schedRes.data.appointments || []);
      
      // Fetch Slots (Availability created by this doctor)
      const slotRes = await axios.get(`${API_BASE}/slotsservice/getproviderslots`, { headers: { Token: token }});
      if(slotRes.data.code === 200) setSlots(slotRes.data.slots || []);
      
    } catch (err) {
      toast.error("Error loading dashboard data.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const createSlot = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post(`${API_BASE}/slotsservice/createslot`, {
        slotDate: newSlot.date,
        startTime: newSlot.start,
        endTime: newSlot.end
      }, { headers: { Token: token }});
      
      if(res.data.code === 200) {
        toast.success("Time slot added!");
        setNewSlot({ date: '', start: '', end: '' });
        fetchData();
      } else {
        toast.error(res.data.message);
      }
    } catch (err) {
      toast.error("Failed to create slot");
    }
  };

  return (
    <div className="dashboard-container animate-fade-in-up">
      <div className="header delay-1">
        <div>
          <h1 style={{ fontSize: '2rem', fontWeight: 600 }}>Welcome, Dr. {currentUser?.fullname}</h1>
          <p style={{ color: 'var(--text-secondary)' }}>Provider Dashboard</p>
        </div>
        <button className="btn btn-secondary" onClick={logout}>
          <LogOut size={18} /> Logout
        </button>
      </div>

      <div className="grid-2">
        {/* Left Column: Manage Availability */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          
          <div className="glass-card animate-fade-in-up delay-2">
            <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
              <Plus size={22} color="var(--accent)" /> Add Availability
            </h2>
            <form onSubmit={createSlot} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-secondary)' }}>Date</label>
                <input type="date" className="input-field" value={newSlot.date} onChange={e => setNewSlot({...newSlot, date: e.target.value})} required />
              </div>
              <div style={{ display: 'flex', gap: '1rem' }}>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-secondary)' }}>Start Time</label>
                  <input type="time" className="input-field" value={newSlot.start} onChange={e => setNewSlot({...newSlot, start: e.target.value})} required />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-secondary)' }}>End Time</label>
                  <input type="time" className="input-field" value={newSlot.end} onChange={e => setNewSlot({...newSlot, end: e.target.value})} required />
                </div>
              </div>
              <button type="submit" className="btn" style={{ marginTop: '0.5rem' }}>Create Slot</button>
            </form>
          </div>

          <div className="glass-card animate-fade-in-up delay-3">
            <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
              <Calendar size={22} color="var(--accent)" /> My Active Slots
            </h2>
            {loading ? <p>Loading...</p> : slots.length === 0 ? <p>No slots created.</p> : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {slots.map((s, idx) => (
                  <div key={idx} style={{ background: 'rgba(255,255,255,0.02)', padding: '1rem', borderRadius: '8px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <p style={{ color: 'var(--text-primary)', fontWeight: 500 }}>{s.slotDate}</p>
                      <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{s.startTime} - {s.endTime}</p>
                    </div>
                    <span className={`tag ${s.isAvailable ? 'tag-success' : 'tag-error'}`}>
                      {s.isAvailable ? 'Available' : 'Booked'}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>

        {/* Right Column: Upcoming Patients */}
        <div className="glass-card animate-fade-in-up delay-2">
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1.5rem' }}>
            <Users size={22} color="var(--accent)" /> Upcoming Appointments
          </h2>
          {loading ? <p>Loading schedule...</p> : schedule.length === 0 ? <p>No appointments booked yet.</p> : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {schedule.map((a, idx) => (
                <div key={idx} style={{ borderBottom: '1px solid var(--glass-border)', paddingBottom: '1rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <h4 style={{ fontWeight: 500 }}>{a.clientName || 'Patient'}</h4>
                    <span className={`tag ${a.status === 'BOOKED' ? 'tag-success' : 'tag-pending'}`}>{a.status}</span>
                  </div>
                  <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '0.5rem' }}>
                    {a.slotDate} at {a.startTime}
                  </p>
                  {a.notes && <p style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', marginTop: '0.5rem', fontStyle: 'italic' }}>Notes: {a.notes}</p>}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
