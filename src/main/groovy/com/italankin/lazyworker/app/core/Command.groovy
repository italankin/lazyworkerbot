package com.italankin.lazyworker.app.core

import io.fouad.jtb.core.TelegramBotApi
import io.fouad.jtb.core.beans.Message
import io.fouad.jtb.core.builders.ApiBuilder
import io.fouad.jtb.core.enums.ParseMode

class Command {

    private final TelegramBotApi api
    private final int i
    private final Message message
    private final String name
    private final String rawArgs

    Command(TelegramBotApi api, int i, Message message) {
        this.api = api
        this.i = i
        this.message = message
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
        return message.getFrom().getId()
    }

    long getChatId() {
        return message.getChat().getId()
    }

    boolean reply(String message) {
        ApiBuilder.api(api)
                .sendMessage(message)
                .toChatId(getChatId())
                .parseMessageAs(ParseMode.MARKDOWN)
                .execute()
        return true
    }

}
