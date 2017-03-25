package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler

class StartHandler implements Handler {

    @Override
    String name() {
        return "start"
    }

    @Override
    boolean handle(Command command) throws Exception {
        return command.reply("Use /help to see list of available commands.")
    }

    @Override
    String helpMessage() {
        return null
    }

}
