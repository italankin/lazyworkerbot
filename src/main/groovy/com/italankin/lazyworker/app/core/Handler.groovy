package com.italankin.lazyworker.app.core

interface Handler {

    String name()

    boolean handle(Command command) throws Exception

    String helpMessage()

}
