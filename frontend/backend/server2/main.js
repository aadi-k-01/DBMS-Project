import express from 'express'
import dotenv from 'dotenv'
import cors from 'cors'
import {conn} from './config/connectDB.js'
import taskController from './controller/controller.js'
import nodeController from './controller/nodeController.js'

dotenv.config()

const app = express()
app.use(cors())
app.use(express.json())

//routing to task controller
app.use('/tasks', taskController)

//routing to node controller (logs, preferences, providers)
app.use('/node', nodeController)

// Connect to database
await conn()

app.get("/", (req, res)=>{
    res.json({"code": 200, "message": "server is running!"})
});

const PORT = process.env.PORT || 8002

app.listen(PORT, ()=>{
    console.log(`Server running in port ${PORT}`)
})