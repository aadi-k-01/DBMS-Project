import mongoose from 'mongoose'
import dotenv from 'dotenv'

dotenv.config()

const url = process.env.MONGO_URL

let db

export const conn = async () => {
    try{
        if(!db)
            db = await mongoose.connect(url)
        return db
    }catch(err){
        console.log(err)
    }
}