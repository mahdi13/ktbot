# ktbot
Kotlin Telegram bot DSL, using FSA (finite-state automaton)

This library helps you make flexible Telegram bots.
1. Design your Bot flow using a `finite-state automaton`.
2. Implement it using a few available keywords of this DSL.
3. Add some complex logic, database storage, etc. to your bot!

*Note: This library is extremely `experimental` and `under-development`!*

```kotlin
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
```