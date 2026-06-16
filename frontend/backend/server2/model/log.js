import mongoose from 'mongoose'

const logSchema = new mongoose.Schema({
    action: {type: String, required: true},
    userId: {type: Number, required: true},
    appointmentId: {type: Number},
    details: {type: String}
}, {
    timestamps: true
});

export const Log = mongoose.model('Log', logSchema)
