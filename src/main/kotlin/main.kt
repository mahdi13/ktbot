import com.perfect.telegram.bot
import com.perfect.telegram.botContext
import telegram.InMemoryBotDao
import telegram.simpleInlineState

fun main() {
    botContext {
        bot("your_username", "your_token", InMemoryBotDao()) {
            simpleInlineState("1") {

                commandMatcher = "/start".toRegex()

                subject { "Hello World!" }
                content { "How are you @${it.message.chat.userName} ?" }

            }
        }
    }
}