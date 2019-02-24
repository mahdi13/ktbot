import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.logging.BotLogger

fun String.bold() = "<b>$this</b>"

fun SendMessage.setChatId(lastUpdate: Update) = setChatId(lastUpdate.message.chatId)!!

fun SendMessage.sendCarefully(sender: AbsSender) {
    try {
        sender.execute(this)
    } catch (e: TelegramApiException) {
        BotLogger.error(this::class.simpleName, e)
    }
}

fun EditMessageText.setChatId(lastUpdate: Update) = setChatId(lastUpdate.callbackQuery.message.chatId)!!

fun EditMessageText.sendCarefully(sender: AbsSender) {
    try {
        sender.execute(this)
    } catch (e: TelegramApiException) {
        BotLogger.error(this::class.simpleName, e)
    }
}

