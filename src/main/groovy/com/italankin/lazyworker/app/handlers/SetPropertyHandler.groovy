package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.activity.User
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request

class SetPropertyHandler implements Handler {

    private final ActivityManager activityManager

    SetPropertyHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "owner_setpreference"
    }

    @Override
    boolean handle(Request request) throws Exception {
        int userId = request.getSenderId()
        User user = activityManager.getUser(userId)
        if (user.level < User.LEVEL_OWNER) {
            throw new RuntimeException("Operation not permitted for userId=$userId")
        }
        String[] args = request.rawArgs.split("\\s+", 3)
        int targetUserId = args[0].toInteger()
        User.Preference preference = activityManager.setUserPreference(targetUserId, args[1], args[2])
        return request.response("$targetUserId, ${preference.key}, ${preference.value}")
    }

    @Override
    String helpMessage() {
        throw new RuntimeException()
    }

}
