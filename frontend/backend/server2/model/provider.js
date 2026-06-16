import mongoose from 'mongoose'

const providerSchema = new mongoose.Schema({
    providerId: {type: Number, required: true, unique: true},
    providerName: {type: String, required: true},
    specialty: {type: String},
    bio: {type: String},
    vector: {type: [Number]}
}, {
    timestamps: true
});

export const Provider = mongoose.model('Provider', providerSchema)
