package com.perfect.telegram

import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.telegram.telegrambots.meta.api.objects.Update
import java.lang.Exception

enum class MessageType(val explaination: String, val validate: (Update) -> Boolean) {
    CALLBACK(
        "Callback, use the provided keys",
        { update -> update.hasCallbackQuery() }
    ),
    ANY(
        "Any response",
        { true }
    ),
    ANY_MESSAGE(
        "Any types of message",
        { update -> update.hasMessage() }
    ),
    TEXT(
        "Text message",
        { update -> update.hasMessage() && update.message.hasText() }
    ),
    NON_EMPTY_TEXT(
        "Non empty text messages",
        { update -> update.hasMessage() && update.message.hasText() && update.message.text.isNotEmpty() }
    ),
    DOUBLE(
        "Floating point number",
        { update -> update.hasMessage() && update.message.hasText() && update.message.text.toDoubleOrNull() != null }
    ),
    INT(
        "Decimal number",
        { update -> update.hasMessage() && update.message.hasText() && update.message.text.toIntOrNull() != null }
    ),
    FULL_NAME(
        "Full Name, at least 2 parts, e.g. `Jack Daniel`",
        { update -> update.hasMessage() && update.message.hasText() && update.message.text.split(" ").size > 1 }
    ),
    MANUAL_PHONE_NUMBER(
        "Enter your phone number, including country code, e.g. `+909876543210`",
        { update -> update.hasMessage() && update.message.hasText() && update.message.text.trim().isPhone() }
    ),
    PASSPORT_DATA(
        "Passport data which be presented by telegram",
        { update -> update.hasMessage() && update.message.hasPassportData() && update.message.passportData != null }
    ),
    IMAGE(
        "Any kind of image (documents are not acceptable), by phones camera",
        { update -> update.hasMessage() && update.message.hasPhoto() && update.message.photo != null && update.message.photo.size > 0 }
    ),
    SHORT_VIDEO(
        "A short video, by phones camera",
        { update -> update.hasMessage() && update.message.hasPhoto() && update.message.photo != null && update.message.photo.size > 0 }
    ),
}

fun String.isPhone(): Boolean = try {
    PhoneNumberUtil.getInstance().parse(this, null);true
} catch (e: Exception) {
    false
}
