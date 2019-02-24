package com.perfect.telegram

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.util.ArrayList


class InlineKeyboardBuilder private constructor() {

    private var chatId: Long? = null
    private var text: String? = null

    private val keyboard = ArrayList<List<InlineKeyboardButton>>()
    private var row: MutableList<InlineKeyboardButton>? = null

    fun setText(text: String): InlineKeyboardBuilder {
        this.text = text
        return this
    }

    fun setChatId(chatId: Long?): InlineKeyboardBuilder {
        this.chatId = chatId
        return this
    }

    fun row(): InlineKeyboardBuilder {
        this.row = ArrayList()
        return this
    }

    fun button(text: String, callbackData: String): InlineKeyboardBuilder {
        row!!.add(InlineKeyboardButton().setText(text).setCallbackData(callbackData))
        return this
    }

    fun button(customButton: InlineKeyboardButton): InlineKeyboardBuilder {
        row!!.add(customButton)
        return this
    }

    fun endRow(): InlineKeyboardBuilder {
        this.keyboard.add(this.row!!)
        this.row = null
        return this
    }


    fun build(): SendMessage {
        val message = SendMessage()

        message.setChatId(chatId!!)
        message.text = text

        val keyboardMarkup = InlineKeyboardMarkup()

        keyboardMarkup.keyboard = keyboard
        message.replyMarkup = keyboardMarkup

        return message
    }

    fun buildEdited(messageId: Int): EditMessageText {
        val message = EditMessageText()

        message.messageId = messageId
        message.setChatId(chatId!!)
        message.text = text

        val keyboardMarkup = InlineKeyboardMarkup()

        keyboardMarkup.keyboard = keyboard
        message.replyMarkup = keyboardMarkup

        return message
    }

    companion object {
        fun create(chatId: Long?): InlineKeyboardBuilder {
            val builder = InlineKeyboardBuilder()
            builder.setChatId(chatId)
            return builder
        }
    }

}
