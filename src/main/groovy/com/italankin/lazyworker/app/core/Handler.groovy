package com.italankin.lazyworker.app.core

interface Handler {

    String name()

    boolean handle(Request request) throws Exception

    String helpMessage()

}
