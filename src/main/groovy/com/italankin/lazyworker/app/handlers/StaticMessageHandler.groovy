package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request
import io.fouad.jtb.core.enums.ParseMode

class StaticMessageHandler implements Handler {
    private final String message
    private final ParseMode parseMode

    StaticMessageHandler(String message) {
        this(message, ParseMode.MARKDOWN)
    }

    StaticMessageHandler(String message, ParseMode parseMode) {
        this.parseMode = parseMode
        this.message = message
    }

    @Override
    String name() {
        return null
    }

    @Override
    boolean handle(Request request) throws Exception {
        return request.response(message, parseMode)
    }

    @Override
    String helpMessage() {
        return null
    }
}
