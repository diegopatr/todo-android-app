package br.com.exemplo.todo.data.model

data class Task(
    var id: String?, // Document ID (will be generated by Firestore)
    val description: String
) {
    constructor() : this(
        id = null,
        description = ""
    )
    constructor(description: String) : this(id = null, description = description)
}