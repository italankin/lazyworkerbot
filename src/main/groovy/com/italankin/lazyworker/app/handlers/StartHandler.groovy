package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request

class StartHandler implements Handler {

    @Override
    String name() {
        return "start"
    }

    @Override
    boolean handle(Request request) throws Exception {
        return request.response("Use /help to see list of available commands.")
    }

    @Override
    String helpMessage() {
        return null
    }

}
