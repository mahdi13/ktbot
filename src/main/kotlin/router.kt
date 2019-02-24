package com.perfect.telegram

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import telegram.UserSession

//abstract class BotRoute(val command: String) {
//
//    fun isMatching(command: String) = command == this.command
//
//    abstract fun respond(command: String, args: ArrayList<String>): InlineKeyboardBuilder
//
//}

enum class Route {
    HOME,
    UNVERIFIED_HOME,
    ASK_PHONE,
    ASK_PASSPORT_PICTURE,
    ASK_VERIFICATION_PICTURE,

    VERIFIED_HOME,
    EXCHANGE_LIST,
    EXCHANGE_PRICE,
    EXCHANGE_,
}

fun unverifiedHome(session: UserSession) = InlineKeyboardBuilder.create(session.chatId)
    .setText("Welcome to BOCHUM EXCHANGE !!\n\n")

    .row()
    .setText("I am talking with ${session.firstName} ${session.lastName}. We will call you @${session.username} here!")
    .endRow()

    .row().button("Let's get started !!!", Route.ASK_PHONE.toString()).endRow()


fun askPhone(session: UserSession): SendMessage {
    val message = SendMessage()

    message.setChatId(session.chatId!!)
    message.text = "Salam"

    val keyboard = ArrayList<KeyboardRow>()
    keyboard.add(KeyboardRow().apply { add(KeyboardButton().setText("Verify phone").setRequestContact(true)) })

    val keyboardMarkup = ReplyKeyboardMarkup()
    keyboardMarkup.keyboard = keyboard

    message.replyMarkup = keyboardMarkup

    return message
}


//fun askPhone(session: SessionDao) = InlineKeyboardBuilder.create(session.chatId)
//    .setText("For the first step we need to verify your mobile phone! !!\n\n")
//
//    .row()
//    .setText("I am talking with ${session.firstName} ${session.lastName}. We will call you @${session.username} here!")
//    .endRow()
//
//    .row()
//    .button("Verify my mobile phone !!!", RoutingPage.ASK_PASSPORT_PICTURE.toString())
//    .endRow()


fun verifiedHome(session: UserSession) = InlineKeyboardBuilder.create(session.chatId)
    .setText("Welcome to BOCHUM EXCHANGE !!\n\n")

    .row()
    .setText("I am talking with ${session.firstName} ${session.lastName}. We will call you @${session.username} here!")
    .endRow()

    .row().button("Let's get started !!!", "lets-go").endRow()