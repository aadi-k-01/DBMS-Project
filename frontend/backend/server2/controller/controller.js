import express from 'express'
import * as taskService from '../service/taskService.js'

const router = express.Router()

router.post("/createtask", async (req, res)=>{
    //console.log(req.body)
    res.json(await taskService.createTask(req.body, req.headers["token"]))
});

router.get("/getalltasks/:PAGE/:SIZE", async (req, res)=>{
    res.json(await taskService.getAllTasks(req.headers["token"], parseInt(req.params.PAGE), parseInt(req.params.SIZE)))
});

router.delete("/deletetask/:ID", async (req, res)=>{
    res.json(await taskService.deleteById(req.headers.token, req.params.ID))
});

router.get("/gettask/:ID", async (req, res)=>{
    res.json(await taskService.findById(req.headers.token, req.params.ID))
});

router.put("/updatetask/:ID", async (req, res)=>{
    res.json(await taskService.updateById(req.headers.token, req.params.ID, req.body))
});

router.get("/vectorsearch/:QUERY", async (req, res)=>{
    res.json(await taskService.vectorSearch(req.headers.token, req.params.QUERY))
});

export default router