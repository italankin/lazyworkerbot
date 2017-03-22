package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.core.Command
import com.italankin.lazyworker.app.core.Handler

class StartCommandHandler implements Handler {

    @Override
    String name() {
        return "start"
    }

    @Override
    boolean handle(Command command) throws Exception {
        return command.reply("I'm listening.")
    }

    @Override
    String helpMessage() {
        return null
    }

}
