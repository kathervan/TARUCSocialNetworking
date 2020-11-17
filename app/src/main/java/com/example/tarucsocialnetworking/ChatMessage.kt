package com.example.tarucsocialnetworking

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String){
    constructor() : this("","", "", "")
}
