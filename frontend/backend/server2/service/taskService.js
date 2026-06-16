import {Task} from '../model/task.js'
import jwt from 'jsonwebtoken'
import dotenv from 'dotenv'
import {generateVector, cosineSimilarity} from './vectorService.js'

dotenv.config()

const secret = process.env.SECRET_KEY

export const createTask = async (data, token)=>{
    let response = {}
    try {
        const payload = jwt.verify(token, secret)
        data.createdby = payload.id
        data.vector = await generateVector(data.title + " " + data.description)
        console.log("create task data:")
        console.log(data)
        await Task.create(data)
        response = {code: 200, message: "Task created successfully!"}
    }
    catch (error) {
        response = {code: 500, message: error.message}
    }
    return response
}

export const getAllTasks = async (token, page, size)=>{
    let response = {}
    try {
        const payload = jwt.verify(token, secret)
        const skip = (page-1) * size;
        const tasks = await Task.find()
                            .skip(skip)
                            .limit(size)
                            .sort({_id: 1}); //sor by decending means -1
        const totalrecords = await Task.countDocuments()
        response = {code: 200, page: page, size: size, totalpages: Math.ceil(totalrecords/size), tasks: tasks};
    } catch (error) {
        response = {code: 500, message: error.message}
    }
    return response;
}

export const deleteById = async (token, id)=>{
    let response = {}
    try {
        const payload = jwt.verify(token, secret)
        await Task.findOneAndDelete({_id: id})
        response = {code: 200, message: "Task deleted successfully!"}
    } catch (error) {
        response = {code: 500, message: error.message}
    }
    return response;
}

export const findById = async (token, id)=>{
    let response = {}
    try {
        const payload = jwt.verify(token, secret)
        const task = await Task.findById({_id: id})
        response = {code: 200, task: task}
    } catch (error) {
        response = {code: 500, message: error.message}
    }
    return response;
}

export const updateById = async (token, id, data)=>{
    let response = {}
    try {
        const payload = jwt.verify(token, secret)
        data.vector = await generateVector(data.title + " " + data.description)
        await Task.findOneAndUpdate({_id: id}, data)
        response = {code: 200, message: "Task updated successfully!"}
    } catch (error) {
        response = {code: 500, message: error.message}
    }
    return response;
}

export const vectorSearch = async (token, query)=>{
    let response = {}
    try{
        const payload = jwt.verify(token, secret)
        const queryVector = await generateVector(query)
        const tasks = await Task.find()
        const similarTasks = tasks.map(task => {
            const similarity = cosineSimilarity(queryVector, task.vector)
            return {...task._doc, similarity}
        })
        .filter(task => task.similarity > 0.10) // Adjust threshold as needed
        .sort((a, b) => b.similarity - a.similarity) // Sort by similarity
        .slice(0, 5); // Limit to top 5 results
        response = {code: 200, tasks: similarTasks}

    } catch(error){
        response = {code: 500, message: error.message}
    }
    return response;
}

//function createTask() {}