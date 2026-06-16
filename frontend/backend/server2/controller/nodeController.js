import express from 'express'
import {Log} from '../model/log.js'
import {Preference} from '../model/preference.js'
import {Provider} from '../model/provider.js'
import {generateVector, cosineSimilarity} from '../service/vectorService.js'

const router = express.Router()

// ---- Audit Logs ----

router.post("/logs", async (req, res) => {
    try {
        const log = await Log.create(req.body)
        res.json({code: 200, message: "Log created", log})
    } catch (error) {
        res.status(500).json({code: 500, message: error.message})
    }
})

router.get("/logs", async (req, res) => {
    try {
        const logs = await Log.find().sort({createdAt: -1}).limit(100)
        res.json({code: 200, logs})
    } catch (error) {
        res.status(500).json({code: 500, message: error.message})
    }
})

// ---- User Preferences ----

router.get("/preferences/:userId", async (req, res) => {
    try {
        const pref = await Preference.findOne({userId: parseInt(req.params.userId)})
        if (!pref) {
            return res.json({code: 404, message: "Preferences not found"})
        }
        res.json({code: 200, preferences: pref})
    } catch (error) {
        res.status(500).json({code: 500, message: error.message})
    }
})

router.put("/preferences/:userId", async (req, res) => {
    try {
        const pref = await Preference.findOneAndUpdate(
            {userId: parseInt(req.params.userId)},
            req.body,
            {new: true, upsert: true}
        )
        res.json({code: 200, message: "Preferences updated", preferences: pref})
    } catch (error) {
        res.status(500).json({code: 500, message: error.message})
    }
})

// ---- Provider Profiles ----

router.post("/providers", async (req, res) => {
    try {
        const {providerId, providerName, specialty, bio} = req.body
        // Generate vector embedding for semantic search
        let vector = []
        try {
            vector = await generateVector(`${providerName} ${specialty} ${bio}`)
        } catch (e) {
            console.warn("Vector generation failed, saving without vector:", e.message)
        }
        
        const provider = await Provider.findOneAndUpdate(
            {providerId},
            {providerId, providerName, specialty, bio, vector},
            {new: true, upsert: true}
        )
        res.json({code: 200, message: "Provider synced", provider})
    } catch (error) {
        res.status(500).json({code: 500, message: error.message})
    }
})

router.get("/providers/search", async (req, res) => {
    try {
        const q = req.query.q || ""
        if (!q) {
            const providers = await Provider.find()
            return res.json({code: 200, providers})
        }
        
        let queryVector
        try {
            queryVector = await generateVector(q)
        } catch (e) {
            // Fallback to text search if vector generation fails
            const providers = await Provider.find({
                $or: [
                    {providerName: {$regex: q, $options: 'i'}},
                    {specialty: {$regex: q, $options: 'i'}},
                    {bio: {$regex: q, $options: 'i'}}
                ]
            })
            return res.json({code: 200, providers})
        }

        const providers = await Provider.find()
        const results = providers
            .filter(p => p.vector && p.vector.length > 0)
            .map(p => {
                const similarity = cosineSimilarity(queryVector, p.vector)
                return {...p._doc, similarity}
            })
            .filter(p => p.similarity > 0.10)
            .sort((a, b) => b.similarity - a.similarity)
            .slice(0, 10)
        
        res.json({code: 200, providers: results})
    } catch (error) {
        res.status(500).json({code: 500, message: error.message})
    }
})

export default router
