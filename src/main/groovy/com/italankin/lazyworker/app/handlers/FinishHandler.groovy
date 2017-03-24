package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Command

class FinishHandler extends AbstractFinishHandler {

    FinishHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "finish"
    }

    @Override
    boolean handle(Command command) throws Exception {
        if(!finish(command)) {
            return command.reply("No current activity found.")
        }
        return true
    }

    @Override
    String helpMessage() {
        return null
    }

}
