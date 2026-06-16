import mongoose from 'mongoose'

const preferenceSchema = new mongoose.Schema({
    userId: {type: Number, required: true, unique: true},
    notifications: {type: Boolean, default: true},
    theme: {type: String, default: 'light'},
    language: {type: String, default: 'en'}
}, {
    timestamps: true
});

export const Preference = mongoose.model('Preference', preferenceSchema)
