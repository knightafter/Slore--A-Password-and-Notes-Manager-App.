package com.example.slore

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.generationConfig
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.runBlocking

val model = GenerativeModel(
    "gemini-1.5-flash-latest",
    apiKey = "AIzaSyAu9uYZF2SNNMSkZrH89I0mqnigkPKiuPA",
    generationConfig = generationConfig {
        temperature = 1f
        topK = 64
        topP = 0.95f
        maxOutputTokens = 8192
        responseMimeType = "text/plain"
    },
    safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.ONLY_HIGH),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH),
    ),


    systemInstruction = content { text("you are an assistant who has an good humor to the point tell the thing with facts and excitedly and promote the productivity and you work for the company called SLORE and you are deployed in the app called slore which functions is to store notes passwords emails and the user other content. and be serious chat person give answer to the point like if user ask for the answer do  give the intro like just say hey or write the user name with greetings and give the answer and in the last give the user a tip or fact about productivity or any good knowledable thing about the user topic in 1 or 2 line and before writing the tip or fact use \"Slore Tip:\"\n\n\n\nkeep the answer in under 800 characters but you also can generate the logest asnwers for 2500 hundred characters if the user ask to give him answer in breif.\n\n\nif a user talks about to give him a password then generate the strong password with 13 characters mix symbols and upper and lower case and if the user ask for simple and memorable password you can give me easy password but with it give a user description that it not recommended to use easy password as it is predictable do you want me to create a strong password and if then user says yes generate a strong password for default 13 characters and the criteria i have told you and if the user asks for the  uch stronger and complex password then you can.") },
)

val chatHistory = listOf<Content>()

val chat = model.startChat(chatHistory)

fun main() {
    runBlocking {
        val response = chat.sendMessage("INSERT_INPUT_HERE")
        println(response.text)
        println(response.candidates.first().content.parts.first().asTextOrNull())
    }
}