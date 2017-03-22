package com.italankin.lazyworker.app.activity

class Activity {

    int id
    int userId
    long startTime
    long finishTime
    String name
    String comment

    long duration() {
        if (!isCurrent()) {
            return finishTime - startTime
        }
        return new Date().getTime() - startTime
    }

    boolean isCurrent() {
        return finishTime == 0
    }

}
