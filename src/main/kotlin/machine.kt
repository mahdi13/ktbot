package com.perfect.telegram

import bold
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.logging.BotLogger
import telegram.UserSession
import java.lang.Exception

class BadMessageTypeException(message: String?) : Exception(message)
class InvalidStateTypeException(message: String?) : Exception(message)


open class BotState(
    val id: String,
    var messageMatcher: Regex? = null,
    var commandMatcher: Regex? = null,
    var callbackQueryMatcher: Regex? = null,
    var latestStateMatcher: Regex = ".*".toRegex(),
    var responseAsEdit: Boolean = false,
    var acceptableTypes: List<MessageType> = listOf(MessageType.ANY),
    var title: String = id,
    var description: String = "This id $id"
) {

    open fun validateState(userSession: UserSession): Boolean = true

    open fun validateType(update: Update): Boolean = acceptableTypes.find { it.validate(update) } != null

    open fun isMatched(update: Update, session: UserSession) = when {
        (!this.latestStateMatcher.matches(session.latestState)) -> false

        (this.messageMatcher != null && update.hasMessage() && this.messageMatcher!!.matches(update.message.text)) -> true

        (this.commandMatcher != null && update.hasMessage() && update.message.isCommand && this.commandMatcher!!.matches(
            update.message.text
        )) -> true

        (this.callbackQueryMatcher != null && update.hasCallbackQuery() && (update.callbackQuery.data ?: update.callbackQuery.id != null) && this.callbackQueryMatcher!!.matches(
            update.callbackQuery.data ?: update.callbackQuery.id
        )) -> true

        else -> false
    }

    var executor: ((update: Update, session: UserSession, sender: AbsSender, error: String?) -> Unit)? =
        { update: Update, session: UserSession, sender: AbsSender, error: String? ->
            try {
                sender.execute(
                    SendMessage()
                        .setChatId(session.chatId.toString())
                        .enableHtml(true)
                        .setText(title.bold() + "\n" + description)
                )
            } catch (e: TelegramApiException) {
                BotLogger.error(this::class.simpleName, e)
            }
        }

    @ContextDsl
    fun executor(executor: (update: Update, session: UserSession, sender: AbsSender, error: String?) -> Unit) =
        apply { this.executor = executor }

    open fun execute(update: Update, session: UserSession, sender: AbsSender, error: String? = null) =
        executor?.invoke(update, session, sender, error)

    fun isCommand() = commandMatcher != null

}

