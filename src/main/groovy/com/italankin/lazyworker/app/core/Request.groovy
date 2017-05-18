package com.italankin.lazyworker.app.core

import io.fouad.jtb.core.TelegramBotApi
import io.fouad.jtb.core.beans.CallbackQuery
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
    private final CallbackQuery callbackQuery
    private final String name
    private final String rawArgs
    private final int senderId
    private final long chatId

    Request(TelegramBotApi api, int updateId, Message message) {
        this(api, updateId, message, null)
    }

    Request(TelegramBotApi api, int updateId, CallbackQuery callbackQuery) {
        this(api, updateId, callbackQuery.getMessage(), callbackQuery)
    }

    Request(TelegramBotApi api, int updateId, Message message, CallbackQuery callbackQuery) {
        this.api = api
        this.updateId = updateId
        this.message = message
        this.senderId = callbackQuery?.getFrom()?.getId() ?: message.getFrom().getId()
        this.chatId = message.getChat().getId()
        this.callbackQuery = null
        String text = callbackQuery?.data ?: message.getText()
        if (text[0] != '/') {
            name = null
            rawArgs = null
            return
        }
        int si = text.indexOf(' ')
        if (si == -1) {
            String command = text.substring(1).toLowerCase()
            int i = command.indexOf('@')
            name = i != -1 ? command.substring(0, i) : command
            rawArgs = null
            return
        }
        String command = text.substring(1, si).toLowerCase()
        int i = command.indexOf('@')
        name = i != -1 ? command.substring(0, i) : command
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
        return api()
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
        return api()
                .sendDocument(is, name)
                .toChatId(chatId)
                .execute()
    }

    ApiBuilder.ApiTopLevel api() {
        return ApiBuilder.api(api)
    }

    TelegramBotApi getApi() {
        return api
    }

    Message getMessage() {
        return message
    }

    CallbackQuery getCallbackQuery() {
        return callbackQuery
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
                ", message='${message.getText()}'" +
                '}'
    }

}
