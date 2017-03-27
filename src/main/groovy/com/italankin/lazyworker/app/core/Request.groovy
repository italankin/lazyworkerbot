package com.italankin.lazyworker.app.core

import io.fouad.jtb.core.TelegramBotApi
import io.fouad.jtb.core.beans.Message
import io.fouad.jtb.core.builders.ApiBuilder
import io.fouad.jtb.core.enums.ParseMode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Request {

    private static final Logger LOG = LoggerFactory.getLogger(Request.class)

    private final TelegramBotApi api
    private final int updateId
    private final Message message
    private final String name
    private final String rawArgs
    private final int senderId
    private final long chatId

    Request(TelegramBotApi api, int updateId, Message message) {
        this.api = api
        this.updateId = updateId
        this.message = message
        this.senderId = message.getFrom().getId()
        this.chatId = message.getChat().getId()
        String text = message.getText()
        if (text[0] != '/') {
            name = null
            rawArgs = null
            return
        }
        int si = text.indexOf(' ')
        if (si == -1) {
            name = text.substring(1).toLowerCase()
            rawArgs = null
            return
        }
        name = text.substring(1, si).toLowerCase()
        rawArgs = text.substring(si).trim()
    }

    Message response(String message) {
        return response(chatId, message, ParseMode.MARKDOWN)
    }

    Message response(String message, ParseMode mode) {
        return response(chatId, message, mode)
    }

    Message response(long chatId, String message, ParseMode mode) {
        LOG.info("Sending message:\n$message")
        return ApiBuilder.api(api)
                .sendMessage(message)
                .toChatId(chatId)
                .parseMessageAs(mode)
                .execute()
    }

    Message response(InputStream is, String name) {
        LOG.info("Sending document: $name")
        return response(chatId, is, name)
    }

    Message response(long chatId, InputStream is, String name) {
        return ApiBuilder.api(api)
                .sendDocument(is, name)
                .toChatId(chatId)
                .execute()
    }

    TelegramBotApi getApi() {
        return api
    }

    Message getMessage() {
        return message
    }

    String getName() {
        return name
    }

    String getRawArgs() {
        return rawArgs
    }

    int getSenderId() {
        return senderId
    }

    @Override
    String toString() {
        return "Request{" +
                "updateId=$updateId" +
                ", name='$name'" +
                ", rawArgs='$rawArgs'" +
                ", senderId='$senderId'" +
                '}'
    }

}
