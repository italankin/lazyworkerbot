package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.activity.Activity
import com.italankin.lazyworker.app.activity.ActivityManager
import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.Request
import com.italankin.lazyworker.app.utils.DateUtils

class TotalHandler implements Handler {

    private final ActivityManager activityManager

    TotalHandler(ActivityManager activityManager) {
        this.activityManager = activityManager
    }

    @Override
    String name() {
        return "total"
    }

    @Override
    boolean handle(Request request) throws Exception {
        String query = request.getRawArgs()
        if (query == null || query.isEmpty()) {
            return request.response(helpMessage())
        }
        if (!query.matches("(?-i)[\\p{L}.,_ -:;]+")) {
            return request.response("Query contains illegal characters")
        }
        List<Activity> activities = activityManager.search(request.getSenderId(), query)
        long total = activities.sum(0L) { it.duration() }
        return request.response("Total duration: _${DateUtils.pretty(total)}_ (${activities.size()} activities)")
    }

    @Override
    String helpMessage() {
        return "Usage: /total query\n\n" +
                "_query_ - search query"
    }
}
