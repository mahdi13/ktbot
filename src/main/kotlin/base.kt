package com.perfect.telegram

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import telegram.UserSession
import telegram.BotSessionDao
import telegram.InlineBotState
import java.lang.Exception

class BotException(message: String?) : Exception(message)

class TelegramRouterLongPollingBot(
    private val username: String,
    private val token: String,
    private val sessionProvider: BotSessionDao
) :
    TelegramLongPollingBot() {

    val router: BotRoute = BotRoute()

    override fun getBotUsername(): String = username

    override fun getBotToken(): String = token

    override fun onUpdateReceived(update: Update?) {
        if (update == null) return

        try {
            val message = (update.message ?: update.callbackQuery.message)
            router.route(
                update,
                sessionProvider.findSessionById(message.chat.id) ?: sessionProvider.createSession(
                    id = message.chat.id,
                    chatId = message.chatId.toString(),
                    firstName = message.chat.firstName,
                    lastName = message.chat.lastName,
                    username = message.chat.userName
                ),
                this@TelegramRouterLongPollingBot
            )
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    @ContextDsl
    fun state(id: String, body: BotState.() -> Unit) = router.addState(BotState(id).apply(body))

    @ContextDsl
    fun state(state: BotState) = router.addState(state)

}

@ContextDsl
fun bot(
    username: String,
    token: String,
    sessionProvider: BotSessionDao,
    body: TelegramRouterLongPollingBot.() -> Unit
) =
    TelegramRouterLongPollingBot(username, token, sessionProvider).apply(body)

@ContextDsl
fun botContext(botBuilder: () -> TelegramRouterLongPollingBot) {
    ApiContextInitializer.init()
    val botsApi = TelegramBotsApi()
    try {
        botsApi.registerBot(botBuilder())
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }

}

open class BotRoute {
    val states = ArrayList<BotState>()
    var tryAgain: BotState? = null
    var notFoundState: BotState? = null


    fun addState(state: BotState) = states.add(state)


    fun route(update: Update, session: UserSession, sender: AbsSender): Unit? {
        try {
            val validState = findMatchingState(update, session)
            return when {
                validState != null -> {
                    validState.execute(update, session, sender)
                    session.latestState = validState.id
                }
                else -> notFoundState?.execute(update, session, sender)
            }
        } catch (e: BadMessageTypeException) {
            // TODO: Append error message
        } catch (e: InvalidStateTypeException) {
            // TODO: Remove session data
            // TODO: Append error message
        }
        return tryAgain?.execute(update, session, sender)
    }

    private fun findMatchingState(update: Update, session: UserSession) = states.find { state ->
        if (!state.isMatched(update, session)) false
        else if (!state.validateType(update)) throw BadMessageTypeException("Message type doesn't match any of ${state.acceptableTypes}")
        else if (!state.validateState(session)) throw InvalidStateTypeException("It seems the data corrupted, try again")
        else true
    }
}

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class ContextDsl