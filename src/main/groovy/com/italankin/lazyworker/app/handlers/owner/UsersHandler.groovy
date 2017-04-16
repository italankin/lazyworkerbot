package com.italankin.lazyworker.app.handlers.owner

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.activity.User
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request

class UsersHandler implements Handler {

    private final ActivityManager activityManager

    UsersHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "users"
    }

    @Override
    boolean handle(Request request) throws Exception {
        List<User> users = activityManager.getAllUsers()
        StringBuilder sb = new StringBuilder()
        for (User user : users) {
            sb.append(user.id)
            sb.append("|")
            sb.append(user.level)
            sb.append("\n")
        }
        return request.response(sb.toString())
    }

    @Override
    String helpMessage() {
        throw new RuntimeException()
    }

}
