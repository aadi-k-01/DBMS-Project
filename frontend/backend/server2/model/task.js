import mongoose from 'mongoose'

const taskSchema = new mongoose.Schema({
    title: {type: String, required: true},
    description: {type: String, required: true},
    assignedto: {type: Number, required: true},
    priority: {type: Number, required: true},
    deadline: {type: String, required: false},
    status: {type: Number, required: true},
    createdby: {type: Number, required: true},
    vector: {type: [Number]}
}, {
    timestamps: {createdAt: 'createdat', updatedAt: 'updatedat'}
});

export const Task = mongoose.model('Task', taskSchema)