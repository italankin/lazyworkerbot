package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Request

class FinishHandler extends AbstractFinishHandler {

    FinishHandler(ActivityManager activityManager) {
        super(activityManager)
    }

    @Override
    String name() {
        return "finish"
    }

    @Override
    boolean handle(Request request) throws Exception {
        if(!finish(request)) {
            return request.response("No current activity found.")
        }
        return true
    }

    @Override
    String helpMessage() {
        return "Usage: /finish"
    }

}
