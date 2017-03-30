package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.activity.User
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request

class StartHandler implements Handler {

    private final ActivityManager activityManager

    StartHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "start"
    }

    @Override
    boolean handle(Request request) throws Exception {
        activityManager.createUser(request.getSenderId(), User.LEVEL_USER)
        return request.response("Use /help to see list of available commands.")
    }

    @Override
    String helpMessage() {
        return null
    }

}
