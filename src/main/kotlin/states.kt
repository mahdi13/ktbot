package telegram

import bold
import com.perfect.telegram.BotState
import com.perfect.telegram.ContextDsl
import com.perfect.telegram.InlineKeyboardBuilder
import com.perfect.telegram.TelegramRouterLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import sendCarefully
import setChatId
import java.util.ArrayList

class InlineBotStateKeyboard {

    private val rows: MutableList<InlineBotStateKeyboardRow> = mutableListOf()

    @ContextDsl
    fun row(body: InlineBotStateKeyboardRow.() -> Unit) = rows.add(InlineBotStateKeyboardRow().apply(body))

    fun buildMarkup(): InlineKeyboardMarkup = InlineKeyboardMarkup().setKeyboard(rows.map { it.buttons })
}

class InlineBotStateKeyboardRow {

    val buttons: MutableList<InlineKeyboardButton> = mutableListOf()

    @ContextDsl
    fun button(body: InlineKeyboardButton.() -> Unit) = buttons.add(InlineKeyboardButton().apply(body))

}

class InlineBotState(id: String) : BotState(id) {

    private var messageTitleBuilder: ((Update) -> String)? = null
    private var messageContentBuilder: ((Update) -> String)? = null

    private var matchConditions: MutableList<InlineBotState.() -> Boolean> = mutableListOf()
    private var validationConditions: MutableList<InlineBotState.() -> Boolean> = mutableListOf()

    //    private var keyboard: InlineBotStateKeyboard = InlineBotStateKeyboard()
    private var keyboardBuilder: (InlineBotStateKeyboard.(Update) -> Unit)? = null

    @ContextDsl
    fun subject(builder: (Update) -> String) {
        messageTitleBuilder = builder
    }

    @ContextDsl
    fun content(builder: (Update) -> String) {
        messageContentBuilder = builder
    }

    final override fun validateState(userSession: UserSession): Boolean =
        super.validateState(userSession) && !validationConditions.any { !it() }

    final override fun isMatched(update: Update, session: UserSession): Boolean =
        super.isMatched(update, session) && !matchConditions.any { !it() }

    final override fun validateType(update: Update): Boolean = super.validateType(update)

    @ContextDsl
    fun addMatchingCondition(condition: InlineBotState.() -> Boolean) = matchConditions.add(condition)

    @ContextDsl
    fun addValidationCondition(condition: InlineBotState.() -> Boolean) = validationConditions.add(condition)

    @ContextDsl
    fun keyboard(builder: InlineBotStateKeyboard.(Update) -> Unit) = apply { keyboardBuilder = builder }

    final override fun execute(update: Update, session: UserSession, sender: AbsSender, error: String?): Unit? {
        val keyboard = InlineBotStateKeyboard().apply { keyboardBuilder?.invoke(this, update) }
        if (responseAsEdit) buildEdited(update, keyboard, update.callbackQuery.message.messageId)
            .setChatId(update)
            .sendCarefully(sender)
        else build(update, keyboard)
            .setChatId(update)
            .sendCarefully(sender)
        return null
    }

    private fun buildText(update: Update) = StringBuilder()
        .append(messageTitleBuilder?.invoke(update)?.bold())
        .append("\n\n")
        .append(messageContentBuilder?.invoke(update))
        .append("\n")
        .toString()


    private fun build(update: Update, keyboard: InlineBotStateKeyboard): SendMessage {
        val message = SendMessage()
        message.text = buildText(update)
        message.enableHtml(true)
        message.replyMarkup = keyboard.buildMarkup()
        return message
    }

    private fun buildEdited(update: Update, keyboard: InlineBotStateKeyboard, messageId: Int): EditMessageText {
        val message = EditMessageText()
        message.messageId = messageId
        message.text = buildText(update)
        message.enableHtml(true)
        message.replyMarkup = keyboard.buildMarkup()
        return message
    }

}

@ContextDsl
fun TelegramRouterLongPollingBot.simpleInlineState(id: String, body: InlineBotState.() -> Unit) =
    router.addState(InlineBotState(id).apply(body))
