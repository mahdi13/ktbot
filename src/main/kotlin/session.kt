package telegram


interface UserSession {
    var username: String
    var firstName: String
    var lastName: String
    var chatId: Long
    var payload: Map<String, Any>
    var latestState: String
}

class SimpleUserSession(
    override var username: String,
    override var firstName: String,
    override var lastName: String,
    override var chatId: Long,
    override var payload: Map<String, Any>,
    override var latestState: String
) : UserSession

interface BotSessionDao {
    fun findSessionById(id: Long): UserSession?
    fun createSession(
        id: Long,
        firstName: String,
        lastName: String,
        username: String,
        chatId: String
    ): UserSession
}

class InMemoryBotDao : BotSessionDao {

    private val sessions: MutableList<SimpleUserSession> = mutableListOf()

    override fun findSessionById(id: Long): SimpleUserSession? = sessions.find { it.chatId == id }

    override fun createSession(
        id: Long,
        firstName: String,
        lastName: String,
        username: String,
        chatId: String
    ): UserSession =
        SimpleUserSession(
            chatId = id,
            firstName = firstName,
            lastName = lastName,
            username = username,
            latestState = "",
            payload = emptyMap()
        ).apply { sessions.add(this) }

}

